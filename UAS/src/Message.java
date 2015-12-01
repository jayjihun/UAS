public interface Message
{
	
}

class UniversityInsertSuccessMessage implements Message
{
	public String toString()
	{
		return "A university is successfully inserted";
	}
}

class UniversityDeleteSuccessMessage implements Message
{
	public String toString()
	{
		return "A university is successfully deleted";
	}
}

class StudentInsertSuccessMessage implements Message
{
	public String toString()
	{
		return "A student is successfully inserted";
	}
}

class StudentDeleteSuccessMessage implements Message
{
	public String toString()
	{
		return "A student is successfully deleted";
	}
}

class CapacityRangeErrorMessage implements Message
{
	public String toString()
	{
		return "Capacity should be over 0.";
	}
}

class GroupRangeErrorMessage implements Message
{
	public String toString()
	{
		return "Group should be \'A\', \'B\', or \'C\'."; 
	}
}

class GPAPortionRangeErrorMessage implements Message
{
	public String toString()
	{
		return "Weight of high school records cannot be nagative.";
	}
}

class SATRangeErrorMessage implements Message
{
	public String toString()
	{
		return "CSAT score should be between 0 and 400.";
	}
}

class GPARangeErrorMessage implements Message
{
	public String toString()
	{
		return "High school records score should be between 0 and 100.";
	}
}

class ApplicationSuccessMessage implements Message
{
	public String toString()
	{
		return "Successfully made an application.";
	}
}

class ApplicationFailedMessage implements Message
{
	public String toString()
	{
		return "A student can apply up to one university per group";
	}
}

class MenuSelectErrorMessage implements Message
{
	public String toString()
	{
		return "Invalid action.";
	}
}

class UniversityIDErrorMessage implements Message
{
	private Object uniid;
	public UniversityIDErrorMessage(Object id)
	{
		uniid = id;
	}
	
	public String toString()
	{
		return "University "+uniid+" doesn\'t exist.";
	}
}

class StudentIDErrorMessage implements Message
{
	private Object stuid;
	public StudentIDErrorMessage(Object id)
	{
		stuid = id;
	}
	
	public String toString()
	{
		return "Student "+stuid+" doesn\'t exist.";
	}
}

class ComplicatedMessage implements Message
{
	public String toString()
	{
		return "WTF!";
	}
}