package com.cm.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.web.common.ErrorCode;
import com.cm.web.constant.CommonConstant;
import com.cm.web.exception.BusinessException;
import com.cm.web.exception.ThrowUtils;
import com.cm.web.mapper.GeneratorMapper;
import com.cm.web.model.dto.generator.GeneratorQueryRequest;
import com.cm.web.model.entity.Generator;
import com.cm.web.model.entity.User;
import com.cm.web.model.vo.GeneratorVO;
import com.cm.web.model.vo.UserVO;
import com.cm.web.service.GeneratorService;
import com.cm.web.service.UserService;
import com.cm.web.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 */
@Service
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator>
    implements GeneratorService {
    @Resource
    private UserService userService;

    @Override
    public void validGenerator(Generator generator, boolean add) {
        if (generator == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = generator.getName();
        String description = generator.getDescription();

        //创建时，参数不能为空
        if (add){
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name,description),ErrorCode.PARAMS_ERROR);
        }
        //有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 256){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"描述过长");
        }
    }

    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {
        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();
        if (generatorQueryRequest == null){
            return queryWrapper;
        }
        String searchText = generatorQueryRequest.getSearchText();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();
        Long id = generatorQueryRequest.getId();

        String name = generatorQueryRequest.getName();
        String description = generatorQueryRequest.getDescription();
        List<String> tagsList = generatorQueryRequest.getTags();
        Integer status = generatorQueryRequest.getStatus();

        Long userId = generatorQueryRequest.getUserId();
        Long notId = generatorQueryRequest.getNotId();
        //拼接查询条件
        if (StringUtils.isNotBlank(searchText)){
            queryWrapper.like("name",searchText).or().like("description",searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(name),"name",name);
        queryWrapper.like(StringUtils.isNotBlank(description),"description",description);
        if (CollUtil.isNotEmpty(tagsList)){
            for (String tag : tagsList) {
                queryWrapper.like("tags","\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(status),"status",status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(notId),"id",notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id),"id",id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        return queryWrapper;
    }

    @Override
    public GeneratorVO getGeneratorVO(Generator generator,HttpServletRequest request){
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
        Long generatorId = generator.getId();
        //1、关联查询用户信息
        Long userId = generator.getUserId();
        User user = null;
        if (user != null && userId > 0){
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUser(userVO);
        return generatorVO;
    }


    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request) {
        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVO> generatorVOPage = new Page<>(generatorPage.getCurrent(), generatorPage.getSize(), generatorPage.getTotal());
        if (CollUtil.isEmpty(generatorList)){
            return generatorVOPage;
        }
        //1、关联查询用户信息
        Set<Long> userIdSet = generatorList.stream().map(Generator::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        //填充信息
        List<GeneratorVO> generatorVOList = generatorList.stream().map(generator -> {
            GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
            Long userId = generator.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            generatorVO.setUser(userService.getUserVO(user));
            return generatorVO;
        }).collect(Collectors.toList());
        generatorVOPage.setRecords(generatorVOList);
        return generatorVOPage;
    }

}




