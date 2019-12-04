<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>测试poi</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<!--添加 jq  支持加载-->
	<script	src="${pageContext.request.contextPath }/static/js/jquery_2_1.min.js"></script>
	<script	src="${pageContext.request.contextPath }/static/js/ajaxfileupload.js"></script>
	<script type="text/javascript">
		function uploadFile(file){
			$("span").html("文件上传中，请等待。。。。")
		    $.ajaxFileUpload({  
		        url : "upload_excel", //用于文件上传的服务器端请求地址  
		        secureuri : false, //一般设置为false
		        fileElementId : "file", //文件上传空间的id属性  <input type="file" id="file" name="file" />  
		        type : "post",  
		        dataType : "text", //返回值类型 一般设置为 
		        success : function(result) //服务器成功响应处理函数  
		        {
		        	var result = $.parseJSON(result.replace(/<.*?>/ig,""));
		        	alert(result.msg);
					$("span").html("文件上传成功！")
		        	
		        },  
		        error : function(result)//服务器响应失败处理函数  
		        {  
		        	$("span").html("文件上传失败！")
		        }
		    });  
		    return false;
		}
		function clickFile() {
			$("#file").click();
		}
	</script>
  </head>
  
  <body>
  	
  	
	<button onclick="clickFile()">上传文件</button><span></span>         
    <input type="file" id="file" name="file" onchange ="uploadFile(this)" style="display:none" />
    <br>
    <a href="excel_down">导出excel</a>
    
  </body>
</html>
