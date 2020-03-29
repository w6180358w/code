package org.durcframework.autocode.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.durcframework.autocode.entity.ClientParam;
import org.durcframework.autocode.entity.CodeFile;
import org.durcframework.autocode.entity.DataSourceConfig;
import org.durcframework.autocode.entity.GeneratorParam;
import org.durcframework.autocode.service.DataSourceConfigService;
import org.durcframework.autocode.service.GeneratorService;
import org.durcframework.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 代码生成
 * 
 * @author hc.tang
 * 
 */
@Controller
public class GeneratorController extends BaseController{
	@Autowired
	private GeneratorService generatorService;
	@Autowired
	private DataSourceConfigService dataSourceConfigService;

	@RequestMapping("/generatFile.do")
	public @ResponseBody
	Object generatFile(GeneratorParam generatorParam,HttpServletRequest request) {
		
		List<CodeFile> resultList = null;
		try {
			request.getSession().setAttribute("generatorParam", generatorParam);
			
			DataSourceConfig dataSourceConfig = 
					dataSourceConfigService.get(generatorParam.getDcId());
			
			if(StringUtils.isEmpty(dataSourceConfig.getDbName())) {
				return this.error("请前往[数据源配置]填写数据库名(dbName)");
			}

			resultList = generatorService.generate(generatorParam,dataSourceConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	@RequestMapping("/downloadZip.do")
	public void downloadZip(HttpServletRequest request,HttpServletResponse response) {
		GeneratorParam generatorParam = (GeneratorParam)request.getSession().getAttribute("generatorParam");
		String webRootPath = request.getSession().getServletContext().getRealPath("/");
		
		DataSourceConfig dataSourceConfig = 
				dataSourceConfigService.get(generatorParam.getDcId());

		String zipPath = generatorService.generateZip(generatorParam, dataSourceConfig, webRootPath);
		OutputStream outStream = null;
		BufferedInputStream fis = null;
		try {
			response.setCharacterEncoding("UTF-8");
			File file = new File(zipPath);
			fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			
			response.reset();
			outStream = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + System.currentTimeMillis()+".zip");
			outStream.write(buffer);
			outStream.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if(outStream!=null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	@RequestMapping("/downloadCode.do")
	public void downloadCode(ClientParam clientParam,HttpServletRequest request,HttpServletResponse response) {
		String webRootPath = request.getSession().getServletContext().getRealPath("/");
		
		String zipPath = generatorService.generateClientZip(clientParam, webRootPath);
		
		try {
			String downloadUrl = request.getContextPath() + zipPath.substring(webRootPath.length()).replace("\\", "/");
			response.sendRedirect(downloadUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
