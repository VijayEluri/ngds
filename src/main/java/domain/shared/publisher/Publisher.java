package domain.shared.publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Publisher<S, O extends Subscriber<S, O, A>, A extends Event<S, O, A>> {

	@SuppressWarnings("unchecked")
	private Map<Class<? extends Event>, List<O>> subscribers = new HashMap<Class<? extends Event>, List<O>>();

	public void addSubscriber(O o, A type) {
		@SuppressWarnings("unchecked")
		Class<? extends Event> clazz = type.getClass();
		if (!subscribers.containsKey(clazz)) {
			List<O> newSubscribers = new ArrayList<O>();
			newSubscribers.add(o);
			subscribers.put(clazz, newSubscribers);
		} else {
			List<O> subscribersForClass = subscribers.get(clazz);
			subscribersForClass.add(o);
		}

	}
	
	@SuppressWarnings("unchecked")
	public void publish(A event) {
		for (Class<? extends Event> type : subscribers.keySet()) {
			if (event.getClass() == type) {
				List<O> subscribersForClass = subscribers.get(type);
				for (O subscriber : subscribersForClass) {
					subscriber.handle(event);
				}
			}
		}

	}
}
