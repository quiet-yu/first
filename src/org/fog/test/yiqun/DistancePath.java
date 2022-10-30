package org.fog.test.yiqun;

//解空间的每一个元素，路径和距离的一个结构体
public class DistancePath {
	public int path[];
	public double distance;
	DistancePath(int num){
		this.path=new int[num+1];
		this.distance=0;
	}
}

