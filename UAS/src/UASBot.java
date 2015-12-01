import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
public class UASBot
{
	public int nextUniId;
	public int nextStuId;
	
	private static final String serverName = "147.46.15.238";
	private static final String dbName = "DB-2013-11557";
	private static final String userName = "DB-2013-11557";
	private static final String password = "DB-2013-11557";
	private static final String url = "jdbc:mariadb://" + serverName + "/" + dbName;
	private Connection conn;
	
	public UASBot() throws Exception
	{
		nextUniId = 0;
		nextStuId = 0;
		
		try
		{
			conn = DriverManager.getConnection(url, userName, password);
			
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			throw new Exception("Failed to connect to database.");
		}
	}
	
	public void initialize() throws Exception
	{
		
		String sql1 = "CREATE TABLE IF NOT EXISTS university(u_id int, name varchar(128), capacity int, groupp varchar(2), weight float, applied int, PRIMARY KEY(u_id));";
		String sql2 = "CREATE TABLE IF NOT EXISTS student(s_id int, name varchar(20), csat_score int, school_score int, primary key(s_id));";
		String sql3 = "CREATE TABLE IF NOT EXISTS application(s_id int, u_id int, group_u varchar(2), primary key(s_id,group_u), foreign key (s_id) REFERENCES student(s_id), FOREIGN KEY (u_id) REFERENCES university(u_id));";
		String sql4 = "SELECT max(u_id) as max FROM university;";
		String sql5 = "SELECT max(s_id) as max FROM student;";
		PreparedStatement stmt1,stmt2,stmt3,stmt4,stmt5, stmt;
		
		try
		{
			stmt1 = conn.prepareStatement(sql1);
			stmt2 = conn.prepareStatement(sql2);
			stmt3 = conn.prepareStatement(sql3);
			stmt4 = conn.prepareStatement(sql4);
			stmt5 = conn.prepareStatement(sql5);
			
			stmt1.executeQuery();
			stmt2.executeQuery();
			stmt3.executeQuery();
			ResultSet rs;
			rs = stmt4.executeQuery();
			int uid_max = 1;
			if(rs.next())
				uid_max =rs.getInt("max");
			rs = stmt5.executeQuery();
			int sid_max = 1;
			if (rs.next())
				sid_max = rs.getInt("max");
			nextUniId = uid_max+1;
			nextStuId = sid_max+1;
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			throw new Exception("Failed to initiate tables");
		}		
	}
	
	public void close()
	{
		try
		{
			conn.close();
		}
		catch(SQLException e)
		{
			pl("Failed to close..");
		}
	}
	/*
	 * Schema information
	 * 1. University
	 * u_id / name / capacity / group / weight / applied
	 * 
	 * 2. Student
	 * s_id / name / csat_score / school_score
	 * 
	 * 3. application
	 * s_id / u_id / group 
	 * 
	 * 
	 * 
	 */
	
	//====================================================================================================
	//====================================================================================================
	//====================================================================================================
	//====================================================================================================
	//====================================================================================================
	
	public void print_all_unis()
	{
		String sql = "SELECT * FROM university;";
		try
		{
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			Vector<University> unis = fetch_unis(rs);
			print_unis(unis);
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
		}
		catch(NullPointerException e)
		{
			
		}
	}
	
	public void print_all_stus()
	{
		String sql = "SELECT * FROM student;";
		try
		{
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			Vector<Student> stus = fetch_stus(rs);
			print_stus(stus);
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			
		}
		catch(NullPointerException e)
		{
			
		}
	}
	
	public Message insert_uni(String name, String cap_s, String group_s, String gpaportion_s)
	{
		int cap;
		char group;
		float gpaportion;
		try	{cap = Integer.parseInt(cap_s);}catch(NumberFormatException e){return new CapacityRangeErrorMessage();}
		try {gpaportion = Float.parseFloat(gpaportion_s);}catch(NumberFormatException e){return new GPAPortionRangeErrorMessage();}
		
		if(group_s.length() != 1)
			return new GroupRangeErrorMessage();
		group = group_s.charAt(0);
		
		//now cap,group,gpaportion are type-valid
		University uni = new University(nextUniId,name,cap,group,gpaportion,0);
		Message temp = check_uni(uni);
		if(temp != null)
			return temp;
		
		//now uni is semantic-valid
		try
		{
			String sql = "INSERT INTO university values(?,?,?,?,?,?);";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, uni.id);
			stmt.setString(2, uni.name);
			stmt.setInt(3, uni.cap);
			stmt.setString(4, Character.toString(uni.group));
			stmt.setFloat(5, uni.gpaportion);
			stmt.setInt(6, uni.applied);
			stmt.executeUpdate();
			nextUniId++;
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			return new ComplicatedMessage();
		}	
		
		return new UniversityInsertSuccessMessage();
	}
	
	public Message remove_uni(String id_s)
	{
		int id = 0;
		try{id=Integer.parseInt(id_s);}catch(NumberFormatException e){return new UniversityIDErrorMessage(id_s);}
		try
		{
			String sql ="DELETE FROM application WHERE u_id = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			
			String sql2 = "DELETE FROM university WHERE u_id = ?;"; 
			stmt = conn.prepareStatement(sql2);
			stmt.setInt(1, id);;
			stmt.executeUpdate();
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			return new ComplicatedMessage();
		}
		return new UniversityDeleteSuccessMessage();
	}
	
	public Message insert_stu(String name, String csat_s, String gpa_s)
	{
		int csat=0,gpa=0;
		try{csat=Integer.parseInt(csat_s);}catch(NumberFormatException e){return new SATRangeErrorMessage();}
		try{gpa=Integer.parseInt(gpa_s);}catch(NumberFormatException e){return new GPARangeErrorMessage();}
		
		//attributes are now type-valid.
		Student stu = new Student(nextStuId,name,csat,gpa);
		Message temp = check_stu(stu);
		if(temp != null)
			return temp;
		
		//stu is semantic-valid.
		try
		{
			String sql = "INSERT INTO student values(?,?,?,?);";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, stu.id);
			stmt.setString(2, stu.name);
			stmt.setInt(3, stu.csat);
			stmt.setInt(4, stu.gpa);			
			stmt.executeUpdate();
			nextStuId++;
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			return new ComplicatedMessage();
		}
		
		return new StudentInsertSuccessMessage();
	}
	
	public Message remove_stu(String id_s)
	{
		int id = 0;
		try{id=Integer.parseInt(id_s);}catch(NumberFormatException e){return new StudentIDErrorMessage(id_s);}
		try
		{
			String sql = "UPDATE university AS u2 SET u2.applied = u2.applied-1 WHERE u2.u_id IN (SELECT * FROM (SELECT u1.u_id FROM university AS u1 JOIN application USING (u_id) WHERE s_id = ?) temp);"; 
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			
			
			sql = "DELETE FROM application WHERE s_id = ?;";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			
			
			sql = "DELETE FROM student WHERE s_id = ?;";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.executeUpdate();
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			return new ComplicatedMessage();
		}
		return new StudentDeleteSuccessMessage();
	}
	
	public Message make_app(String s_id_s, String u_id_s)
	{
		/*
		 * Make an application.
		 * - can apply more than capacity.
		 * - each student can apply up to one uni for each Group.
		 * 
		 * 1. First check if id are valid.
		 * 2. Try to insert to Application Table.
		 * 3. If insert, modify University Table. (increase 'applied')
		 * 
		 */
		int s_id=0, u_id=0;
		University uni=null;
		try{s_id=Integer.parseInt(s_id_s);}catch(NumberFormatException e){return new StudentIDErrorMessage(s_id_s);}
		try{u_id=Integer.parseInt(u_id_s);}catch(NumberFormatException e){return new UniversityIDErrorMessage(u_id_s);}
		if(!is_uni_exist(u_id))
			return new UniversityIDErrorMessage(u_id);
		if(!is_stu_exist(s_id))
			return new StudentIDErrorMessage(s_id);
		try
		{
			uni = get_uni(u_id);
			char group = uni.group;
			String sql = "INSERT INTO application VALUES(?,?,?);";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, s_id);
			stmt.setInt(2, u_id);
			stmt.setString(3, Character.toString(group));
			stmt.executeUpdate();
			
			//successfully applied. now increase applied of uni.
			int newapplied = uni.applied+1;
			sql = "UPDATE university SET applied=? WHERE u_id=?;";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, newapplied);
			stmt.setInt(2, u_id);
			stmt.executeUpdate();
			
			return new ApplicationSuccessMessage();
		}
		catch(SQLException e)
		{
			return new ApplicationFailedMessage();
		}
	}
	
	public Message print_applicants(String id_s)
	{
		int u_id = 0;
		try{u_id=Integer.parseInt(id_s);}catch(NumberFormatException e){return new UniversityIDErrorMessage(id_s);}
		try
		{
			if(!is_uni_exist(u_id))
				return new UniversityIDErrorMessage(id_s);
			String sql = "SELECT s_id,name,csat_score,school_score FROM student NATURAL JOIN application WHERE u_id=?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, u_id);
			ResultSet rs = stmt.executeQuery();
			Vector<Student> stus= fetch_stus(rs);
			print_stus(stus);
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			return new ComplicatedMessage();
		}
		
		return null;
	}
	
	public Message print_applied_unis(String id_s)
	{
		int s_id = 0;
		try{s_id=Integer.parseInt(id_s);}catch(NumberFormatException e){return new StudentIDErrorMessage(id_s);}
		if(!is_stu_exist(s_id))
			return new StudentIDErrorMessage(id_s);
		try
		{
			
			String sql = "SELECT u_id,name,capacity,groupp,weight,applied FROM university NATURAL JOIN application where s_id=?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, s_id);
			ResultSet rs = stmt.executeQuery();
			Vector<University> unis = fetch_unis(rs);
			print_unis(unis);			
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			return new ComplicatedMessage();
		}		
		return null;
	}
	
	public Message print_expected_stus(String id_s)
	{
		int u_id = 0;
		try{u_id = Integer.parseInt(id_s);}catch(NumberFormatException e){return new UniversityIDErrorMessage(id_s);}
		if(!is_uni_exist(u_id))
			return new UniversityIDErrorMessage(id_s);
		
		Vector<Student> stus = get_expected_stus(u_id);
		print_stus(stus);		
		return null;
	}
	
	public Message print_expected_unis(String id_s)
	{
		/*
		 * for unis which I applied to,
		 * test if i am expected. 
		 */
		int s_id = 0;
		try{s_id = Integer.parseInt(id_s);}catch(NumberFormatException e){return new StudentIDErrorMessage(id_s);}
		if(!is_stu_exist(s_id))
			return new StudentIDErrorMessage(id_s);
		
		//1. for unis which I applied to,
		Vector<University> appliedUnis = null;
		try
		{
			String sql = "SELECT u_id, university.name, capacity, groupp, weight, applied";
			sql += " FROM student as s NATURAL JOIN application JOIN university using (u_id)";
			sql += " WHERE s_id=?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, s_id);
			ResultSet rs = stmt.executeQuery();
			appliedUnis = fetch_unis(rs);
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			return new ComplicatedMessage();
		}
		
		//2. testif i am expected.
		Vector<University> possibleUnis = new Vector<University>(0);
		for(University uni : appliedUnis)
		{
			int u_id = uni.id;
			Vector<Student> exp_stus = get_expected_stus(u_id);
			for(Student stu : exp_stus)
			{
				if(stu.id == s_id)
				{
					possibleUnis.addElement(uni);
					break;
				}
			}
		}
		
		print_unis(possibleUnis);
		return null;
	}
	
	public static void p(Object line)
	{
		System.out.print(line);
	}
	
	public static void pl(Object line)
	{
		System.out.println(line);
	}
	
	private Vector<University> fetch_unis(ResultSet rs) throws SQLException
	{
		Vector<University> v = new Vector<University>(0);
		while(rs.next())
		{
			int id, cap, applied;
			String name;
			char group;
			float gpaportion;
			
			id = rs.getInt("u_id");
			name = rs.getString("name");
			cap = rs.getInt("capacity");
			group = rs.getString("groupp").charAt(0);
			gpaportion = rs.getFloat("weight");
			applied = rs.getInt("applied");
			
			University uni = new University(id, name, cap, group, gpaportion, applied);
			v.addElement(uni);
		}
		return v;
	}
	
	private Vector<Student> fetch_stus(ResultSet rs) throws SQLException
	{
		Vector<Student> v = new Vector<Student>(0);
		while(rs.next())
		{
			int id,csat,gpa;
			String name;
			id = rs.getInt("s_id");
			name = rs.getString("name");
			csat = rs.getInt("csat_score");
			gpa = rs.getInt("school_score");
			
			Student stu = new Student(id,name,csat,gpa);
			v.addElement(stu);
		}
		return v;
	}
	
	private Message check_uni(University uni)
	{
		if(uni.cap<1)
			return new CapacityRangeErrorMessage();
		if(!(uni.group == 'A' || uni.group == 'B' || uni.group == 'C'))
			return new GroupRangeErrorMessage();
		if(uni.gpaportion<0)
			return new GPAPortionRangeErrorMessage();
		return null;
	}
	
	private Message check_stu(Student stu)
	{
		if(stu.csat<0)
			return new SATRangeErrorMessage();
		if(stu.csat>400)
			return new SATRangeErrorMessage();
		if(stu.gpa<0)
			return new GPARangeErrorMessage();
		if(stu.gpa>100)
			return new GPARangeErrorMessage();
		return null;
	}
	
	private boolean is_uni_exist(int id)
	{
		try
		{
			String sql = "SELECT count(*) as cnt FROM university WHERE u_id=?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			int count = 0;
			if(rs.next())
				count = rs.getInt("cnt");//not sure if this is correct.
			return count>=1;
		}
		catch(SQLException e)
		{
			return false;
		}
		
	}
	
	private boolean is_stu_exist(int id)
	{
		try
		{
			String sql = "SELECT count(*) as cnt FROM student WHERE s_id=?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			int count = 0;
			if(rs.next())
				count = rs.getInt("cnt");//not sure if this is correct.
			return count>=1;
		}
		catch(SQLException e)
		{
			return false;
		}
	}
	
	private University get_uni(int id)
	{
		University uni=null;
		try
		{
			String sql = "SELECT * FROM university WHERE u_id=?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next())
			{
				String name = rs.getString("name");
				int cap = rs.getInt("capacity");
				char group = rs.getString("groupp").charAt(0);
				float gpaportion = rs.getFloat("weight");
				int applied = rs.getInt("applied");			
				uni = new University(id,name,cap,group,gpaportion,applied);
				return uni;
			}
			return null;
		}
		catch(SQLException e)
		{
			return null;
		}
	}
	
	private Vector<Student> get_expected_stus(int u_id)
	{
		Vector<Student> stus=null;
		University uni=null;
		try
		{
			String sql = "SELECT s_id, student.name, csat_score, school_score";
			sql+=" FROM university as u NATURAL JOIN application JOIN student using (s_id)";
			sql+=" WHERE u_id = ?";
			sql+=" ORDER BY weight * school_score+csat_score DESC, school_score DESC;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, u_id);
			ResultSet rs = stmt.executeQuery();
			stus = fetch_stus(rs);
			uni = get_uni(u_id);
		}
		catch(SQLException e)
		{
			pl(e.getMessage());
			return null;
		}
		
		int capacity = uni.cap;

		//1. if under capacity, pass them all.
		if(stus.size()<=capacity)
			return stus;
		
		//2. now, applicants are more than capacity.
		int max_capacity = capacity + (int)Math.ceil(0.1*capacity);
		int size = stus.size();
		int end = max_capacity<size ? max_capacity : size-1; 
		int i = end;
		int cut = end;
		int last_gpa = stus.elementAt(end).gpa;
		int last_sat = stus.elementAt(end).csat;
		for(i = end; i>=0; i--)
		{
			boolean different = false;
			Student stu = stus.elementAt(i);
			if (stu.gpa != last_gpa || stu.csat != last_sat)
			{
				cut = i;
				last_gpa = stu.gpa;
				last_sat = stu.csat;
				different = true;
			}
			if(i<=capacity-1)//now probing inside capacity. if not ALLDROP, cut immediately.
			{
				if(cut != max_capacity)// not ALLDROP
					break;
				if(different)// if ALLDROP, cut where difference detected.
					break;
			}
		}
		Vector<Student> cuted = new Vector<Student>(0);
		for(int j=0; j<=cut; j++)
			cuted.addElement(stus.elementAt(j));
		return cuted;
	}
	
	private void print_stus(Vector<Student> stus)
	{
		print_dash();
		pl("id\tname\t\t\t\tcsat_score\tschool_score");
		print_dash();
		for(Student stu : stus)
			pl(stu);
		print_dash();
	}
	
	private void print_unis(Vector<University> unis)
	{
		print_dash();
		pl("id\tname\t\t\t\t\tcapacity\tgroup\tweight\tapplied");
		print_dash();
		for(University uni : unis)
			pl(uni);
		print_dash();
	}
	
	private void print_dash()
	{
		pl("---------------------------------------------------------------------------------------");
	}
}
