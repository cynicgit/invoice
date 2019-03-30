package com.zhongyi.invoice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DecimalFormat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvoiceApplicationTests {

	@Test
	public void contextLoads() {
	}


	public static void main(String[] args) {
		DecimalFormat df = new DecimalFormat("###.##");//设置两位小数

		String num = df.format(1.2);
		System.out.println(num);//这样可以把科学计数法去除!
	}

}

