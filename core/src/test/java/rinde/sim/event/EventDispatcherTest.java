/**
 * 
 */
package rinde.sim.event;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static rinde.sim.event.EventDispatcherTest.EventTypes.EVENT1;
import static rinde.sim.event.EventDispatcherTest.EventTypes.EVENT2;
import static rinde.sim.event.EventDispatcherTest.EventTypes.EVENT3;
import static rinde.sim.event.EventDispatcherTest.OtherEventTypes.OTHER_EVENT1;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Rinde van Lon (rinde.vanlon@cs.kuleuven.be)
 * 
 */
public class EventDispatcherTest {

  enum EventTypes {
    EVENT1, EVENT2, EVENT3
  }

  enum OtherEventTypes {
    OTHER_EVENT1
  }

  ListenerEventHistory l1, l2, l3;

  EventDispatcher dispatcher;
  EventAPI api;

  @Before
  public void setup() {
    l1 = new ListenerEventHistory();
    l2 = new ListenerEventHistory();
    l3 = new ListenerEventHistory();

    dispatcher = new EventDispatcher(EVENT1, EVENT2, EVENT3);
    api = dispatcher.getPublicEventAPI();
  }

  @Test(expected = IllegalArgumentException.class)
  public void dispatchEventFail2() {
    dispatcher.dispatchEvent(new Event(OTHER_EVENT1));
  }

  @Test
  public void dispatchEvent() {
    dispatcher.addListener(l1, EVENT1);

    final Set<Enum<?>> set = new HashSet<Enum<?>>(asList(EVENT1, EVENT2));
    api.addListener(l2, set);
    dispatcher.addListener(l3, EVENT1, EVENT2, EVENT3);

    dispatcher.dispatchEvent(new Event(EVENT2));
    assertEquals(asList(), l1.getEventTypeHistory());
    assertEquals(asList(EVENT2), l2.getEventTypeHistory());
    assertEquals(asList(EVENT2), l3.getEventTypeHistory());

    dispatcher.dispatchEvent(new Event(EVENT3));
    assertEquals(asList(), l1.getEventTypeHistory());
    assertEquals(asList(EVENT2), l2.getEventTypeHistory());
    assertEquals(asList(EVENT2, EVENT3), l3.getEventTypeHistory());

    dispatcher.dispatchEvent(new Event(EVENT1));
    assertEquals(asList(EVENT1), l1.getEventTypeHistory());
    assertEquals(asList(EVENT2, EVENT1), l2.getEventTypeHistory());
    assertEquals(asList(EVENT2, EVENT3, EVENT1), l3.getEventTypeHistory());

    dispatcher.dispatchEvent(new Event(EVENT3));
    assertEquals(asList(EVENT1), l1.getEventTypeHistory());
    assertEquals(asList(EVENT2, EVENT1), l2.getEventTypeHistory());
    assertEquals(asList(EVENT2, EVENT3, EVENT1, EVENT3), l3.getEventTypeHistory());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addListenerFail3() {
    dispatcher.addListener(l1, OTHER_EVENT1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addListenerFail6() {
    dispatcher.addListener(l1, new Enum<?>[] { EVENT2, null, EVENT3 });
  }

  @Test
  public void addListenerToAll() {
    dispatcher.addListener(l1, new Enum<?>[] {});
    assertTrue(dispatcher.containsListener(l1, EVENT1));
    assertTrue(dispatcher.containsListener(l1, EVENT2));
    assertTrue(dispatcher.containsListener(l1, EVENT3));
  }

  @Test
  public void removeListenerForAllTypes() {
    dispatcher.addListener(l1, EVENT1);
    dispatcher.addListener(l2, EVENT1, EVENT2);
    api.addListener(l3, EVENT1, EVENT2, EVENT3);

    assertTrue(api.containsListener(l1, EVENT1));
    assertFalse(api.containsListener(l1, EVENT2));
    assertFalse(api.containsListener(l1, EVENT3));
    assertFalse(api.containsListener(l1, OTHER_EVENT1));

    assertTrue(dispatcher.containsListener(l2, EVENT1));
    assertTrue(dispatcher.containsListener(l2, EVENT2));
    assertFalse(dispatcher.containsListener(l2, EVENT3));
    assertFalse(dispatcher.containsListener(l2, OTHER_EVENT1));

    assertTrue(dispatcher.containsListener(l3, EVENT1));
    assertTrue(dispatcher.containsListener(l3, EVENT2));
    assertTrue(dispatcher.containsListener(l3, EVENT3));
    assertFalse(dispatcher.containsListener(l3, OTHER_EVENT1));

    api.removeListener(l3, new HashSet<Enum<?>>());
    assertFalse(dispatcher.containsListener(l3, EVENT1));
    assertFalse(dispatcher.containsListener(l3, EVENT2));
    assertFalse(dispatcher.containsListener(l3, EVENT3));
    assertFalse(dispatcher.containsListener(l3, OTHER_EVENT1));

    dispatcher.removeListener(l1);
    assertFalse(dispatcher.containsListener(l1, EVENT1));
    assertFalse(dispatcher.containsListener(l1, EVENT2));
    assertFalse(dispatcher.containsListener(l1, EVENT3));
    assertFalse(dispatcher.containsListener(l1, OTHER_EVENT1));

    dispatcher.removeListener(l2);
    assertFalse(dispatcher.containsListener(l2, EVENT1));
    assertFalse(dispatcher.containsListener(l2, EVENT2));
    assertFalse(dispatcher.containsListener(l2, EVENT3));
    assertFalse(dispatcher.containsListener(l2, OTHER_EVENT1));

    dispatcher.removeListener(new ListenerEventHistory());
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeListenerFail3() {
    dispatcher.removeListener(l1, EVENT1);
  }

  @Test
  public void removeListener() {
    dispatcher.addListener(l1, EVENT1);
    dispatcher.addListener(l2, EVENT3, EVENT2);
    dispatcher.addListener(l3, EVENT1, EVENT3);

    assertTrue(dispatcher.containsListener(l1, EVENT1));
    assertFalse(dispatcher.containsListener(l1, EVENT2));
    assertFalse(dispatcher.containsListener(l1, EVENT3));
    assertFalse(dispatcher.containsListener(l1, OTHER_EVENT1));

    assertFalse(dispatcher.containsListener(l2, EVENT1));
    assertTrue(dispatcher.containsListener(l2, EVENT2));
    assertTrue(dispatcher.containsListener(l2, EVENT3));
    assertFalse(dispatcher.containsListener(l2, OTHER_EVENT1));

    assertTrue(dispatcher.containsListener(l3, EVENT1));
    assertFalse(dispatcher.containsListener(l3, EVENT2));
    assertTrue(dispatcher.containsListener(l3, EVENT3));
    assertFalse(dispatcher.containsListener(l3, OTHER_EVENT1));

    dispatcher.removeListener(l2, EVENT2, EVENT3);
    assertFalse(dispatcher.containsListener(l2, EVENT1));
    assertFalse(dispatcher.containsListener(l2, EVENT2));
    assertFalse(dispatcher.containsListener(l2, EVENT3));
    assertFalse(dispatcher.containsListener(l2, OTHER_EVENT1));
  }

  @Test
  public void removeTest() {

    final EventDispatcher disp = new EventDispatcher(EventTypes.values());
    final EventAPI eventAPI = disp.getPublicEventAPI();

    assertTrue(disp.listeners.isEmpty());
    eventAPI.addListener(l1, EVENT1, EVENT2, EVENT3);
    assertEquals(3, disp.listeners.size());
    assertTrue(disp.listeners.containsEntry(EVENT1, l1));
    assertTrue(eventAPI.containsListener(l1, EVENT1));
    assertTrue(eventAPI.containsListener(l1, EVENT2));
    assertTrue(eventAPI.containsListener(l1, EVENT3));

    eventAPI.removeListener(l1);
    assertTrue(disp.listeners.isEmpty());
    assertFalse(eventAPI.containsListener(l1, EVENT1));
    assertFalse(eventAPI.containsListener(l1, EVENT2));
    assertFalse(eventAPI.containsListener(l1, EVENT3));

  }

  @Test(expected = IllegalArgumentException.class)
  public void removeFail() {
    final EventDispatcher disp = new EventDispatcher(EventTypes.values());
    disp.removeListener(l1, EVENT1, null);
  }
}
