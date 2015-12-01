class University
{
	public int id;
	public String name;
	public int cap;
	public char group;
	public float gpaportion;
	public int applied;
	
	public University(int id, String name, int cap, char group, float gpaportion, int applied)
	{
		this.id = id;
		this.name = name;
		this.cap = cap;
		this.group = group;
		this.gpaportion = gpaportion;
		this.applied = applied;
	}
	
	public String toString()
	{
		String result = "";
		result+=id+"\t";
		result+=name+"\t\t\t";
		result+=cap+"\t\t";
		result+=group+"\t";
		result+=gpaportion+"\t";
		result+=applied+"\t";
		return result;
	}
}

class Student
{
	public int id;
	public String name;
	public int csat;
	public int gpa;
	public Student(int id, String name, int csat, int gpa)
	{
		this.id = id;
		this.name = name;
		this.csat = csat;
		this.gpa = gpa;
	}
	
	public String toString()
	{
		String result = "";
		result+=id+"\t";
		result+=name+"\t\t";
		result+=csat+"\t\t";
		result+=gpa;
		return result;
	}
}

class Application
{
	public int s_id;
	public int u_id;
	public char group;
	
	public Application(int s_id, int u_id, char group)
	{
		this.s_id = s_id;
		this.u_id = u_id;
		this.group = group;
	}
}