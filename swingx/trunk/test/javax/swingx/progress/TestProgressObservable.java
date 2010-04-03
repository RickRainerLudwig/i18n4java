package javax.swingx.progress;

public class TestProgressObservable implements ProgressObservable {

	private ProgressObserver observer;
	private boolean startSubProcess = false;

	public TestProgressObservable() {

	}

	public TestProgressObservable(boolean startSubProcess) {
		this.startSubProcess = startSubProcess;
	}

	@Override
	public void setMonitor(ProgressObserver observer) {
		this.observer = observer;
	}

	@Override
	public void run() {
		observer.setRange(0, 9);
		for (int i = 0; i < 10; i++) {
			observer.setStatus(i);
			if ((i == 5) && (startSubProcess)) {
				observer.startSubProgress(new TestProgressObservable());
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				observer.finish();
				return;
			}
			if (Thread.interrupted()) {
				observer.finish();
				return;
			}
		}
		observer.finish();
	}
}