package loadTesting;

public abstract class LoopedExecuter extends Thread{
	protected TestClient callback;
	protected int id;
	protected int counter = 0;
	private int timesPerMin;
	public LoopedExecuter(int id, int timesPerMin, TestClient callback)
	{
		this.id = id;
		this.timesPerMin = timesPerMin;

		this.callback = callback;
	}

	@Override
	public void run() {
		while(callback.isRunning())
		{
			callback.runCommand(getCommand(System.currentTimeMillis() - callback.getCreationTime()));
			try{
				Thread.sleep((long) (60 / timesPerMin * 1000));				
			}
			catch (InterruptedException ex)
			{
				System.out.println("Interrupted sleeping");
			}
		}
	}
	
	protected abstract String getCommand(long timeSinceCreation);
}
