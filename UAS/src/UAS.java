
import java.util.Scanner;
public class UAS
{
	private static final int MENU1 = 1;
	private static final int MENU2 = 2;
	private static final int MENU3 = 3;
	private static final int MENU4 = 4;
	private static final int MENU5 = 5;
	private static final int MENU6 = 6;
	private static final int MENU7 = 7;
	private static final int MENU8 = 8;
	private static final int MENU9 = 9;
	private static final int MENU10 = 10;
	private static final int MENU11 = 11;
	private static final int MENU12 = 12;
	private static final int MENUINVALID = 0;
	
	private UASBot robot;
	private Scanner keyboard;
	
	public static void main(String[] args)
	{
		UAS uas=null;
		try
		{
			uas = new UAS();
		}
		catch(Exception e)
		{
			pl(e.getMessage());
		}
		if(uas!=null)
			uas.start();
	}
	
	public UAS() throws Exception
	{
		robot = new UASBot();
		robot.initialize();
		keyboard = new Scanner(System.in);
	}
	
	public void start()
	{
		while(true)
		{
			printMenu();
			int op = menuselector(keyboard.nextLine());
			switch(op)
			{
			case MENU1:
				print_all_unis();
				break;
			case MENU2:
				print_all_stus();
				break;
			case MENU3:
				insert_uni();
				break;
			case MENU4:
				remove_uni();
				break;
			case MENU5:
				insert_stu();
				break;
			case MENU6:
				remove_stu();
				break;
			case MENU7:
				make_app();
				break;
			case MENU8:
				print_applicants();
				break;
			case MENU9:
				print_applied_unis();
				break;
			case MENU10:
				print_expected_stus();
				break;
			case MENU11:
				print_expected_unis();
				break;
			case MENU12:
				break;
			default:
				pl(new MenuSelectErrorMessage());
				break;
			}
			if(op==MENU12)
				break;
			pl("\n\n");
		}
		keyboard.close();
		robot.close();
	}
	
	public void printMenu()
	{
		pl("============================================================");
		pl("1. print all universities");
		pl("2. print all students");
		pl("3. insert a new university");
		pl("4. remove a university");
		pl("5. insert a new student");
		pl("6. remove a student");
		pl("7. make an application");
		pl("8. print all students who applied for a university");
		pl("9. print all universities a student applied for");
		pl("10. print expected successful applicants of a university");
		pl("11. print universities expected to accept a student");
		pl("12. exit");
		pl("============================================================");
		p("Select your action: ");
	}
	
	public void print_all_unis()
	{
		robot.print_all_unis();
	}
	
	public void print_all_stus()
	{
		robot.print_all_stus();
	}
	
	public void insert_uni()
	{
		String name, cap, group,gpaportion;
		p("University name: ");
		name = keyboard.nextLine();
		p("University capacity: ");
		cap = keyboard.nextLine();
		p("University group: ");
		group = keyboard.nextLine();
		p("Weight of high school records: ");
		gpaportion = keyboard.nextLine();
		Message result = robot.insert_uni(name, cap, group, gpaportion);
		pl(result);
	}
	
	public void remove_uni()
	{
		String id;
		p("University ID: ");
		id = keyboard.nextLine();
		Message result = robot.remove_uni(id);
		pl(result);
	}
	
	public void insert_stu()
	{
		String name, csat,gpa;
		p("Student name: ");
		name = keyboard.nextLine();
		p("CSAT score: ");
		csat = keyboard.nextLine();
		p("High school score:" );
		gpa = keyboard.nextLine();
		Message result = robot.insert_stu(name, csat, gpa);
		pl(result);
	}
	
	public void remove_stu()
	{
		String id;
		p("Student ID: ");
		id = keyboard.nextLine();
		Message result = robot.remove_stu(id);
		pl(result);
	}
	
	public void make_app()
	{
		String s_id, u_id;
		p("Student ID: ");
		s_id = keyboard.nextLine();
		p("University ID: ");
		u_id = keyboard.nextLine();
		Message result = robot.make_app(s_id, u_id);
		pl(result);
	}
	
	public void print_applicants()
	{
		String id;
		p("University ID: ");
		id = keyboard.nextLine();
		Message result = robot.print_applicants(id);
		if(result!=null)
			pl(result);
	}
	
	public void print_applied_unis()
	{
		String id;
		p("Student ID: ");
		id = keyboard.nextLine();
		Message result = robot.print_applied_unis(id);
		if(result!=null)
			pl(result);
	}
	
	public void print_expected_stus()
	{
		String id;
		p("University ID: ");
		id = keyboard.nextLine();
		Message result = robot.print_expected_stus(id);
		if(result!=null)
			pl(result);
	}
	
	public void print_expected_unis()
	{
		String id;
		p("Student ID: ");
		id = keyboard.nextLine();
		Message result = robot.print_expected_unis(id);
		if(result!=null)
			pl(result);
	}

	public int menuselector(String input)
	{
		int result = MENUINVALID;
		try
		{
			result = Integer.parseInt(input);
		}
		catch(NumberFormatException e)
		{
			result = MENUINVALID;
		}
		return result;
	}
	
	public static void p(Object line)
	{
		System.out.print(line);
	}
	
	public static void pl(Object line)
	{
		System.out.println(line);
	}

}