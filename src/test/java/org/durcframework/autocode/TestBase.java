package org.durcframework.autocode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
@TransactionConfiguration(defaultRollback=false)
public class TestBase extends AbstractTransactionalJUnit4SpringContextTests{

	@Override
	@Resource(name="dataSource") 
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public static void main(String[] args) {
		String filetext = "//$张小名$ 25分//@李小花: 43分//@王力: 100分";  
		Pattern p = Pattern.compile("\\$(.*?)\\$");//正则表达式，取=和|之间的字符串，不包括=和|  
		Matcher m = p.matcher(filetext);  
		 while(m.find()) {  
		        System.out.println(m.group(1));//m.group(1)不包括这两个字符  
		  
		   }  
	}

}
