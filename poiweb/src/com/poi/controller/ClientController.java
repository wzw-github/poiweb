package com.poi.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.poi.entity.Client;
import com.poi.service.ClientService;
import com.poi.util.DateUtil;
import com.poi.util.ExcelUtil;
import com.poi.util.FileUtil;
import com.poi.util.ResponseUtil;


@Controller
public class ClientController {
	
	@Autowired
	private ClientService clientService;
	
	/**
	 * 得到前台传来的excel，解析处理后上传到数据库
	 * @param file	前台传来的文件
	 * @param response
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/upload_excel")
	public JSONObject upload_excel(@RequestParam("file") MultipartFile file, HttpServletResponse response,HttpServletRequest request)throws Exception {
		JSONObject result = new JSONObject();
		//上传的文件名称
		System.out.println(file.getOriginalFilename());
		
		if(!file.isEmpty()){
            String webPath=request.getServletContext().getRealPath("");
            String filePath= "/static/upload_file/excel/";
            //把文件名子换成（时间搓.png）
            // String imageName="houtai_logo."+file.getOriginalFilename().split("\\.")[1];
            String fileName=DateUtil.formatDate(new Date(), "yyyyMMdd-HHmmssSSS")+"_"+file.getOriginalFilename();
            FileUtil.makeDirs(webPath+filePath);
            //保存服务器
            file.transferTo(new File(webPath+filePath+fileName));
            //保存服务器
            
            //解析文件得到一个list
            List<Client> list =  excel_to_clientInfo(new File(webPath+filePath+fileName));
            //解析 
            
           //开始 上传 数据库
            for(Client client:list) {
            	clientService.saveClient(client);
            }
            //开始 上传 数据库
            
            //删除用过的文件 
            FileUtil.deleteFile(webPath+filePath+fileName);
            //删除用过的文件 
        }
		result.put("success", true);
		result.put("msg", "导入成功");
		return result;
		
	}
	
	/**
	 * 使用指定的模板，下载excel，网页弹出另存为
	 * @param response
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/excel_down")
	public String excel_down(HttpServletResponse response,HttpServletRequest  request)
			throws Exception {
		//得到项目路径，拼接后用来指定模板的位置
		String webPath=request.getServletContext().getRealPath("/");
		//从数据库查询到数据
		List<Client> list = clientService.findAll();
		
		//传入查询到的数据和模板的位置，然后得到工作表
		Workbook wb = fillExcelDataWithTemplate(list, webPath+"/static/client_down_model.xls");
		
		//把工作表传入工具类，弹出保存，第二个参数工作表，第三个参数工作表默认名称，保存时候可以更改
		ResponseUtil.export(response,wb,"新建Excel.xls");
		return null;
	}
	
	
	
	/**
	 * 解析文件:把数据封装在List中返回
	 * @param userUploadFile
	 * @return List<Client>解析以后封装好的List
	 * @throws ParseException
	 */
	private List<Client> excel_to_clientInfo(File userUploadFile) throws ParseException {
		List<Client> list = new ArrayList<Client>();
		Client client = null;
		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(userUploadFile));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			//获取第一个sheet页 
			HSSFSheet sheet = wb.getSheetAt(0);
			if(sheet!=null){
				//循环操作sheet中的数据，依次取出每行每列的数据保存到List对应的数据中
				for(int rowNum =1;rowNum<=sheet.getLastRowNum();rowNum++){
					HSSFRow row = sheet.getRow(rowNum);
					if(row==null){
						continue;
					}
					client  =new Client();
					//去掉编码中的  .0 如果全是数字 后面有.0
					String bianma = ExcelUtil.formatCell(row.getCell(1));
					client.setBianhao(bianma);
					client.setName(ExcelUtil.formatCell(row.getCell(2)).split("\\.")[0]);
					client.setPhone(ExcelUtil.formatCell(row.getCell(3)));
					client.setRemark(ExcelUtil.formatCell(row.getCell(4)));
					client.setCreateDateTime(ExcelUtil.formatDate(row.getCell(5)));
					System.out.println("hou时间："+client.getCreateDateTime());
					list.add(client);
				}
			}
		}  catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 
	 * @param list	已经拿到的数据
	 * @param templateFileUrl	模板的路径
	 * @return	处理好的excel
	 */
	public static Workbook fillExcelDataWithTemplate(List<Client> list ,String templateFileUrl) {
		
		//新建工作表
		Workbook wb = null ;
		try {
			//通过传入的路径找到模板
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(templateFileUrl));
			//把得到的模板应用在这个新建的工作表
			 wb = new HSSFWorkbook(fs);
			// 取得 模板的 第一个sheet 页
			Sheet sheet = wb.getSheetAt(0);
			// 拿到sheet页有 多少列
			int cellNums = sheet.getRow(0).getLastCellNum();
			// 从第2行 开搞    下标1  就是第2行
			int rowIndex = 1;
			Row row ; 
			//循环处理数据，把传进来的List中的数据保存在指定位置的excel表格中
			for(Client client : list){
				//rowIndex开始时候是1,每次循环会+1
				row = sheet.createRow(rowIndex);
				rowIndex ++;
				
				//这里是用到的模板的excel，知道它有六列，第一列下标为0，从第一列开始
				//第一行是标题行，从第2行开始,第一列是id，循环存入id
				row.createCell(0).setCellValue(client.getId());
				//第一行是标题行，从第2行开始，第二列是bianhao，循环存入bianhao
				row.createCell(1).setCellValue(client.getBianhao());
				//第一行是标题行，从第2行开始，第三列是name，循环存入name
				row.createCell(2).setCellValue(client.getName());
				//第一行是标题行，从第2行开始，第四列是phone，循环存入phone
				row.createCell(3).setCellValue(client.getPhone());
				//第一行是标题行，从第2行开始，第五列是remark，循环存入remark
				row.createCell(4).setCellValue(client.getRemark());
				//第一行是标题行，从第2行开始，第六列是时间，循环存入格式化后的时间
				row.createCell(5).setCellValue(DateUtil.formatDate(client.getCreateDateTime(), "yyyy-MM-dd HH:mm:ss"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//返回处理好的excel
		return wb;
	}
	
	

}
