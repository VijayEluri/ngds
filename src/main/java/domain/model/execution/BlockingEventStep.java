package domain.model.execution;

import java.util.concurrent.CountDownLatch;

import domain.shared.DomainEvent;
import domain.shared.DomainSubscriber;
import domain.shared.EventPublisher;

public class BlockingEventStep extends Executable implements DomainSubscriber<DomainEvent<?>>, Comparable<BlockingEventStep>{

	public static BlockingEventStep factory(EventPublisher publisher, DomainEvent<?> toSend, DomainEvent<?> waitingFor){
		BlockingEventStep retval = new BlockingEventStep(publisher, toSend, waitingFor);
		publisher.addSubscriber(retval, waitingFor);
		return retval;
	}
	private BlockingEventStep(EventPublisher publisher, DomainEvent<?> toSend, DomainEvent<?> waitingFor){
		this.publisher = publisher;
		this.toSend = toSend;
		this.waitingFor = waitingFor;
	}
	private EventPublisher publisher;
	private DomainEvent<?> toSend, waitingFor;
	private CountDownLatch latch = new CountDownLatch(1);
	
	@Override
	public ExitCode execute() {
		publisher.publish(toSend);
		try {
			latch.await();
		} catch (InterruptedException e) {
			//TODO ExitCode.Interrupted?
			return ExitCode.FAILURE;
		}
		return ExitCode.SUCCESS;
	}

	public void handle(DomainEvent event) {
		
		if(event.sameEventAs(waitingFor)){
			latch.countDown();
		}
	}
	
	public int compareTo(BlockingEventStep other){
		return other.hashCode() - hashCode();
	}
	
	public String toString(){
		return "block on " + waitingFor.getClass().getSimpleName() + " following " + toSend.getClass().getSimpleName();
	}

}
