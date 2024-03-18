# ${name}

> ${description}
>
> 作者： ${author}
>
可以通过命令行交互式输入的方式动态生成想要的项目代码

## 使用说明

执行项目根目录下的脚本文件：

```
generator <命令> <选项参数>
```

示例命令：

```
generator generate <#list modelConfig.models as modelInfo> <#if modelInfo.abbr??>-${modelInfo.abbr}</#if></#list>
```

## 参数说明

<#list modelConfig.models as modelInfo>
${modelInfo?index + 1}）<#if modelInfo.fieldName??>${modelInfo.fieldName}</#if>

类型：${modelInfo.type}

描述：${modelInfo.description}

<#if modelInfo.defaultValue??>默认值：<#if modelInfo.defaultValue?is_boolean><#if modelInfo.defaultValue==true>是<#else>否</#if><#else>${modelInfo.defaultValue}</#if></#if>

<#if modelInfo.abbr??>缩写： -${modelInfo.abbr}<#else></#if>

</#list>