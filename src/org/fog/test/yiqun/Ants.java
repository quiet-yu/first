package org.fog.test.yiqun;

//蚂蚁类
public class Ants {
	// 禁忌表, 记录走过的城市
	public boolean table[];
	// 路径
	public int path[];
	//每一个的概率以及累积概率用于轮盘赌
	public double pro[];
	public double accumulatePro[];
	//记录当前路径花费的距离
	public double distance;
	Ants(int number) {
		this.table = new boolean[number];
		this.path = new int[number+1];
		
		for (int i = 0; i < number; i++) {
			this.table[i] = false;
			this.path[i]=-1;
		}
		this.path[number]=-1;
		this.distance=0;
	}
	
	public boolean notOver() {
		for(int i=0;i<this.table.length;i++)
			if(this.table[i]==false)
				return true;
		return false;
	}
}

