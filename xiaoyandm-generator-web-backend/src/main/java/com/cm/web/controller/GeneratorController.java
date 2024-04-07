package com.cm.web.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cm.maker.generator.main.ZipGenerator;
import com.cm.maker.meta.Meta;
import com.cm.maker.meta.MeteValidator;
import com.cm.web.annotation.AuthCheck;
import com.cm.web.common.BaseResponse;
import com.cm.web.common.DeleteRequest;
import com.cm.web.common.ErrorCode;
import com.cm.web.common.ResultUtils;
import com.cm.web.constant.UserConstant;
import com.cm.web.exception.BusinessException;
import com.cm.web.exception.ThrowUtils;
import com.cm.web.manager.CacheManager;
import com.cm.web.manager.CosManager;
import com.cm.web.model.dto.generator.*;
import com.cm.web.model.entity.Generator;
import com.cm.web.model.entity.User;
import com.cm.web.model.vo.GeneratorVO;
import com.cm.web.service.GeneratorService;
import com.cm.web.service.UserService;
import com.google.gson.Gson;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 帖子接口

 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private CacheManager cacheManager;

    private final static Gson GSON = new Gson();

    static String keys = null;

    // region 增删改查

    /**
     * 创建
         * @param generatorAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorAddRequest, generator);
        List<String> tags = generatorAddRequest.getTags();
        if (tags != null) {
            generator.setTags(GSON.toJson(tags));
        }
        generatorService.validGenerator(generator, true);
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        //generator.setFavourNum(0);
        //generator.setThumbNum(0);
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldPost = generatorService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        //删除时同步清理缓存
        cacheManager.clear(keys);
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
         * @param generatorUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        List<String> tags = generatorUpdateRequest.getTags();
        if (tags != null) {
            generator.setTags(GSON.toJson(tags));
        }
        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldPost = generatorService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
         * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     * @param generatorQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
         * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
            HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
         * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
            HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
         * @param generatorEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorEditRequest, generator);
        List<String> tags = generatorEditRequest.getTags();
        if (tags != null) {
            generator.setTags(GSON.toJson(tags));
        }
        // 参数校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        long id = generatorEditRequest.getId();
        // 判断是否存在
        Generator oldPost = generatorService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldPost.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }


    /**
     * 生成器下载接口
     * @param id
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/download")
    public void downloadGeneratorById(long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Generator generator = generatorService.getById(id);
        if (generator == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"产物包不存在");
        }

        //追踪事件
        log.info("用户 {} 下载了 {}", loginUser, distPath);

        //设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + distPath);

        //优先从缓存中读取
        String zipFilePath = getCacheFilePath(id, distPath);
        if (FileUtil.exist(zipFilePath)){
            //写入响应
            Files.copy(Paths.get(zipFilePath),response.getOutputStream());
        }

        COSObjectInputStream cosObjectInput = null;
        try{
            StopWatch stopWatch = new StopWatch();  //用于计算程序执行时间
            stopWatch.start();
            COSObject cosObject = cosManager.getObject(distPath);
            cosObjectInput = cosObject.getObjectContent();
            //处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            stopWatch.stop();
            System.out.println(stopWatch.getTotalTimeMillis());
            //写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        }catch (Exception e){
            log.error("file download error,filepath = ) " + distPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"下载失败");
        }finally {
            if (cosObjectInput != null){
                cosObjectInput.close();
            }
        }
    }


    /**
     * 使用代码生成器
     * @param generatorUseRequest
     * @param request
     * @param response
     */
    @PostMapping("/use")
    public void useGenerator(@RequestBody GeneratorUseRequest generatorUseRequest,
            HttpServletRequest request,HttpServletResponse response) throws IOException {
        //1、获取用户在前端输入的请求参数
        Long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();

        //需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId = {} 使用了生成器 id = {}", loginUser.getId(), id);
        Generator generator = generatorService.getById(id);
        if (generator == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        //2、从对象存储下载生成器的压缩包,到一个独立的工作空间
        String distPath = generator.getDistPath();  //生成器存储路径
        if (StrUtil.isBlank(distPath)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"产物包不存在");
        }

        //定义一个工作空间
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/use/%s", projectPath, id);
        String zipFilePath = tempDirPath + "/dist.zip";

        //不存在则新建文件
        if (!FileUtil.exist(zipFilePath)){
            FileUtil.touch(zipFilePath);
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //下载文件
        try{
            cosManager.download(distPath,zipFilePath);
        }catch (InterruptedException e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"生成器下载失败");
        }
        stopWatch.stop();
        System.out.println("下载耗时：" + stopWatch.getTotalTimeMillis());

        // 3、解压压缩包，得到脚本文件
        stopWatch = new StopWatch();
        stopWatch.start();
        File unzipDistDir = ZipUtil.unzip(zipFilePath);
        stopWatch.stop();
        System.out.println("解压耗时：" + stopWatch.getTotalTimeMillis());

        //4、将用户输入的参数写入到json文件中
        stopWatch = new StopWatch();
        stopWatch.start();
        String dataModelFilePath = tempDirPath + "/dataModel.json";
        String jsonStr = JSONUtil.toJsonStr(dataModel);
        FileUtil.writeUtf8String(jsonStr,dataModelFilePath);
        stopWatch.stop();
        System.out.println("写数据文件耗时：" + stopWatch.getTotalTimeMillis());

        // 5、执行脚本，构造脚本调用命令，传入模型参数 json 文件路径，调用脚本并生成代码
        //找到脚本文件所在路径
        File scriptFile = FileUtil.loopFiles(unzipDistDir,2,null).stream()
                .filter(file -> file.isFile() && "generator.bet".equals(file.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        //添加可执行权限
        try{
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(scriptFile.toPath(),permissions);
        }catch (Exception e){

        }

        //构造命令
        File scriptDir = scriptFile.getParentFile();
        //注意,如果是mca/linux 系统,要用"./generator"
        String[] commands = new String[]{"./generator", "json-generate", "--file=" + dataModelFilePath};
        //String scriptAbsolutePath = scriptFile.getAbsolutePath().replace("\\", "/");
        //String[] commands = {scriptAbsolutePath, "json-generate", "--file=" + dataModelFilePath};
        //拆分
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(scriptDir);

        try{
            stopWatch = new StopWatch();
            stopWatch.start();
            Process process = processBuilder.start();

            //读取命令的输出
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null){
                System.out.println(line);
            }
            //等待命令执行完成
            int exitCode = process.waitFor();
            System.out.println("命令执行结束,退出码:" + exitCode);
            stopWatch.stop();
            System.out.println("执行脚本耗时：" + stopWatch.getTotalTimeMillis());
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"执行生成器脚本错误");
        }

        // 6、返回生成的代码结果压缩包
        //生成代码的位置
        stopWatch = new StopWatch();
        stopWatch.start();
        String generatedPath = scriptDir.getAbsolutePath() + "/generated";
        String resultPath = tempDirPath + "/result.zip";
        File resultFile = ZipUtil.zip(generatedPath, resultPath);
        stopWatch.stop();
        System.out.println("压缩结果耗时：" + stopWatch.getTotalTimeMillis());

        //下载文件
        //设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + resultFile.getName());
        //写入响应
        Files.copy(resultFile.toPath(),response.getOutputStream());
        // 7、清理文件
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
        });
    }


    /**
     * 制作代码生成器
     * @param generatorMakeRequest
     * @param request
     * @param response
     */
    @PostMapping("/make")
    public void makeGenerator(
            @RequestBody GeneratorMakeRequest generatorMakeRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        //1、输入参数
        String zipFilePath = generatorMakeRequest.getZipFilePath();
        Meta meta = generatorMakeRequest.getMeta();

        //需要登录
        User loginUser = userService.getLoginUser(request);

        //2、创建独立工作空间，下载压缩包到本地
        if (StrUtil.isBlank(zipFilePath)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"压缩包不存在");
        }

        //工作空间
        String projectPath = System.getProperty("user.dir");
        //随机id
        String id = IdUtil.getSnowflakeNextId() + RandomUtil.randomString(6);
        String tempDirPath = String.format("%s/.temp/make/%s", projectPath, id);

        String localZipFilePath = tempDirPath + "/project.zip";

        //新建文件
        if (!FileUtil.exist(localZipFilePath)){
            FileUtil.touch(localZipFilePath);
        }

        try{
            cosManager.download(zipFilePath,localZipFilePath);
        }catch (InterruptedException e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"压缩包下载失败");
        }

        //3、解压，得到项目模板文件
        File unzipDistDir = ZipUtil.unzip(localZipFilePath);

        //4、构造meta对象和输出路径
        String sourceRootPath = unzipDistDir.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);
        MeteValidator.doValidAndFill(meta);
        String outputPath = String.format("%s/generated/%s", tempDirPath, meta.getName());

        // 5、调用 maker 方法制作生成器
        ZipGenerator generateTemplate = new ZipGenerator();
        try{
            generateTemplate.doGenerate(meta,outputPath);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"制作失败");
        }

        //6、下载压缩的产物包文件
        String suffix = "-dist.zip";
        String zipFileName = meta.getName() + suffix;
        String distZipFilePath = outputPath + suffix;

        //下载文件
        //设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
        //写入响应
        Files.copy(Paths.get(distZipFilePath),response.getOutputStream());

        //7、清理文件
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
        });
    }


    /**
     * 缓存代码生成器
     * @param generatorCacheRequest
     * @param request
     * @param response
     */
    @PostMapping("/cache")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void cacheGenerator(@RequestBody GeneratorCacheRequest generatorCacheRequest,HttpServletRequest request,HttpServletResponse response){
        if (generatorCacheRequest == null || generatorCacheRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //获取生成器
        long id = generatorCacheRequest.getId();
        Generator generator = generatorService.getById(id);
        if (generator == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        //缓存空间
        String zipFilePath = getCacheFilePath(id, distPath);

        //新建文件
        if (!FileUtil.exist(zipFilePath)){
            FileUtil.touch(zipFilePath);
        }

        //下载生成器
        try{
            cosManager.download(distPath,zipFilePath);
        }catch (InterruptedException e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"压缩包下载失败");
        }
    }


    /**
     * 快速分页获取列表（封装类）
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo/fast")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPageFast(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                     HttpServletRequest request){
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();

        //优先从缓存中读取
        String cacheKey = getPageCacheKey(generatorQueryRequest);
        Object cacheValue = cacheManager.get(cacheKey);
        if (cacheValue != null) {
            return ResultUtils.success((Page<GeneratorVO>) cacheValue);
        }
        //限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Generator> queryWrapper = generatorService.getQueryWrapper(generatorQueryRequest);
        queryWrapper.select("id",
                "name",
                "description",
                "tags",
                "picture",
                "status",
                "userId",
                "createTime",
                "updateTime");

        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),queryWrapper);
        Page<GeneratorVO> generatorVOPage = generatorService.getGeneratorVOPage(generatorPage, request);
        //generatorVOPage.getRecords().forEach(generatorVO -> {
        //    generatorVO.setFileConfig(null);
        //    generatorVO.setModelConfig(null);
        //});
        //写入缓存
        cacheManager.put(cacheKey,generatorVOPage);
        return ResultUtils.success(generatorVOPage);
    }


    /**
     * 获取缓存文件路径
     * @param id
     * @param distPath
     * @return
     */
    public String getCacheFilePath(long id,String distPath){
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/cache/%s", projectPath, id);
        String zipFilePath = String.format("%s/%s", tempDirPath, distPath);
        return zipFilePath;
    }


    /**
     * 获取分页缓存 key
     * @param generatorQueryRequest
     * @return
     */
    public static String getPageCacheKey(GeneratorQueryRequest generatorQueryRequest){
        String jsonStr = JSONUtil.toJsonStr(generatorQueryRequest);
        //请求参数编码
        String base64 = Base64Encoder.encode(jsonStr);
        String key = "generator:page:" + base64;
        keys = key;
        return key;
    }
}
