package com.eastcom_sw.sml.test;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.eastcom_sw.sml.tools.MapUtils;
import com.eastcom_sw.sml.tools.Maps;

public class MapUtilsDemo {
	List<Map<String,Object>> students=MapUtils.newArrayList();
	{
		students.add(new Maps<String,Object>().put("id","0001").put("name","小1").put("class","1").put("age",8).put("score", 90).getMap());
		students.add(new Maps<String,Object>().put("id","0002").put("name","小2").put("class","1").put("age",6).put("score", 60).getMap());
		students.add(new Maps<String,Object>().put("id","0003").put("name","小3").put("class","1").put("age",10).put("score", 100).getMap());
		students.add(new Maps<String,Object>().put("id","0004").put("name","小4").put("class","1").put("age",10).put("score", 98).getMap());
		students.add(new Maps<String,Object>().put("id","0005").put("name","小5").put("class","1").put("age",9).put("score", 87).getMap());
		students.add(new Maps<String,Object>().put("id","0006").put("name","小6").put("class","1").put("age",6).put("score", 78).getMap());
		students.add(new Maps<String,Object>().put("id","0007").put("name","小7").put("class","1").put("age",4).put("score", 99).getMap());
		students.add(new Maps<String,Object>().put("id","0008").put("name","小8").put("class","1").put("age",3).put("score", 88).getMap());
		
		students.add(new Maps<String,Object>().put("id","1001").put("name","大1").put("class","2").put("age",8).put("score", 89).getMap());
		students.add(new Maps<String,Object>().put("id","1002").put("name","大2").put("class","2").put("age",6).put("score", 56).getMap());
		students.add(new Maps<String,Object>().put("id","1003").put("name","大3").put("class","2").put("age",11).put("score", 87).getMap());
		students.add(new Maps<String,Object>().put("id","1004").put("name","大4").put("class","2").put("age",11).put("score", 99).getMap());
		students.add(new Maps<String,Object>().put("id","1005").put("name","大5").put("class","2").put("age",19).put("score", 67).getMap());
		students.add(new Maps<String,Object>().put("id","1006").put("name","大6").put("class","2").put("age",6).put("score", 56).getMap());
		students.add(new Maps<String,Object>().put("id","1007").put("name","大7").put("class","2").put("age",4).put("score", 76).getMap());
		students.add(new Maps<String,Object>().put("id","1008").put("name","大8").put("class","2").put("age",3).put("score", 64).getMap());
	}
	@Test
	public void testMapMethodGrep(){
		System.out.println("grep|年龄大于15岁且班级为二级的学生----->"+MapUtils.grep(students,"--age=> 15 --class=eq 2"));
		
		System.out.println("grep|年龄小于5岁或成绩小于60----->"+MapUtils.grep(students,"--this.type=or --age=< 5 --score=< 60"));
	}
	@Test
	public void testMapMethodAggregate(){
		System.out.println("aggregate|分别找出年龄、成绩最大最小平均值--->"+MapUtils.aggregateLstMp(students,"--age$minAge=min --age$maxAge=max --age$avgAage=avg --score$minS=min --score$maxS=max --score$avgS=avg"));
		System.out.println("aggregate|分别找出每个年级年龄最大最小平均值,成绩总分--->"+MapUtils.aggregateLstMp(students,new String[]{"class"},"--age$minAge=min --age$maxAge=max --age$avgAage=avg --score$total=sum"));
	}
	@Test
	public void testMapMethodGroup(){
		System.out.println("group|按班级分组"+MapUtils.groupMpLst(students,new String[]{"class"}));
		System.out.println("group|按班级,年龄分组"+MapUtils.groupMpLst(students,new String[]{"class","age"}));
	}
	@Test
	public void testMapMethodSort(){
		MapUtils.sort(students,"score","desc");
		System.out.println("sort|按分数进行降序排序！"+students);
	}
}
