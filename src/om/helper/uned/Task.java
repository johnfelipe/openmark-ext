package om.helper.uned;

import om.stdcomponent.uned.TaskComponent;

// UNED: 12-07-2011 - dballestin
/**
 * Class to group the name and parameter of a task to execute.
 */
public class Task
{
	private TaskComponent qcTask;
	
	public Task(TaskComponent qcTask)
	{
		this.qcTask=qcTask;
	}

	/** @return Name of the task to execute */
	public String getTask()
	{
		return qcTask.getTask();
	}
	
	/** @return Parameters of the task to execute */
	public String getParameters()
	{
		return qcTask.getParameters();
	}
	
	/** Execute this task */
	public void execute()
	{
		qcTask.execute();
	}
}
