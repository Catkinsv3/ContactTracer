import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ContactTracerTest {

    @Test
    public void testBulkNull() {

        ContactTracer tracer = new ContactTracer(null);

        assertEquals(Set.of(),
                tracer.contactTrace(null, -999));
    }

    @Test
    public void testDupes() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));

        assertEquals(Set.of("Matt", "Kristian", "Kenton", "Max"),
                tracer.contactTrace("Anna", 130));
    }

    @Test
    public void testNullConstructor() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(null);

        assertEquals(Set.of(), tracer.getContacts("Anna"));
        assertEquals(List.of(), tracer.getContactTimes("Anna", "Kristian"));
        assertEquals(Set.of(), tracer.getContactsAfter("Anna", 0));
        assertEquals(Set.of(), tracer.contactTrace("Anna", 130));
    }

    @Test
    public void testInterspersedNulls() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(null);
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(null);
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(null);
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(null);
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(null);

        assertEquals(Set.of("Matt", "Sanni"), tracer.getContacts("Anna"));
        assertEquals(List.of(100), tracer.getContactTimes("Anna", "Sanni"));
        assertEquals(Set.of("Matt"), tracer.getContactsAfter("Anna", 101));
        assertEquals(Set.of("Matt", "Kristian", "Kenton", "Max"),
                tracer.contactTrace("Anna", 130));
    }

    @Test
    public void testNullParams() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));

        assertEquals(Set.of(), tracer.getContacts(null));
        assertEquals(List.of(), tracer.getContactTimes("Anna", null));
        assertEquals(Set.of(), tracer.getContactsAfter(null, 0));
        assertEquals(Set.of(), tracer.contactTrace(null, 130));
    }

    @Test
    public void testMeetingSelf() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Anna", 100));
        tracer.addTrace(new Trace("Anna", "Anna", 9999));
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));

        assertEquals(Set.of("Sanni", "Matt"), tracer.getContacts("Anna"));
        assertEquals(List.of(), tracer.getContactTimes("Anna", "Anna"));
        assertEquals(Set.of("Matt"), tracer.getContactsAfter("Anna", 200));
        assertEquals(Set.of(), tracer.contactTrace("Anna", 9000));
    }

    @Test
    public void testNegativeParams() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", -999));
        tracer.addTrace(new Trace("Matt", "Kristian", -999));
        tracer.addTrace(new Trace("Kristian", "Sanni", -999));
        tracer.addTrace(new Trace("Kristian", "Kenton", -999));
        tracer.addTrace(new Trace("Kristian", "Max", -999));
        tracer.addTrace(new Trace("Kenton", "Kristian", -999));

        assertEquals(Set.of(), tracer.getContactsAfter("Anna", -100));
        assertEquals(Set.of(), tracer.contactTrace("Anna", -100));
    }

    @Test
    public void testOrderingTime() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("A", "B", 5));
        tracer.addTrace(new Trace("A", "B", 1));
        tracer.addTrace(new Trace("A", "B", 9));
    }

    @Test
    public void testSpecExample() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));

        assertEquals(Set.of("Matt", "Kristian", "Kenton", "Max"),
                tracer.contactTrace("Anna", 130));
    }

    @Test
    public void testSpecExampleBulk() {
        List<Trace> traces = List.of(
                new Trace("Anna", "Sanni", 100),
                new Trace("Anna", "Matt", 1740),
                new Trace("Matt", "Kristian", 3240),
                new Trace("Kristian", "Sanni", 3270),
                new Trace("Kristian", "Kenton", 3360),
                new Trace("Kristian", "Max", 3360),
                new Trace("Kenton", "Kristian", 4020)
        );

        ContactTracer tracer = new ContactTracer(traces);

        assertEquals(Set.of("Matt", "Kristian", "Kenton", "Max"),
                tracer.contactTrace("Anna", 130));
    }

    @Test
    public void testBulkNulls() {

        List<Trace> traces = List.of(
                new Trace(null, null, -100),
                new Trace(null, null, -200),
                new Trace(null, null, -300)
        );

        ContactTracer tracer = new ContactTracer(traces);

        assertEquals(Set.of(),
                tracer.contactTrace(null, -999));
    }

    @Test
    public void myTracerTestAnna() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(new Trace("Anna", "Bob", 0));
        tracer.addTrace(new Trace("Matt", "Sanni", 1800));
        tracer.addTrace(new Trace("Kenton", "Jim", 3420));

        assertEquals(Set.of("Matt", "Kristian", "Kenton", "Max", "Sanni", "Jim"),
                tracer.contactTrace("Anna", 1740));
    }

    @Test
    public void myTracerTestJim() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(new Trace("Anna", "Bob", 0));
        tracer.addTrace(new Trace("Matt", "Sanni", 1800));
        tracer.addTrace(new Trace("Kenton", "Jim", 3420));

        assertEquals(Set.of("Kristian", "Kenton"),
                tracer.contactTrace("Jim", 1));
    }

    @Test
    public void myTracerTestLateInfection() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(new Trace("Anna", "Bob", 0));
        tracer.addTrace(new Trace("Matt", "Sanni", 1800));
        tracer.addTrace(new Trace("Kenton", "Jim", 3420));

        assertEquals(Set.of(),
                tracer.contactTrace("Anna", 99999));
    }

    @Test
    public void myTracerTestAllInfected() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(new Trace("Anna", "Bob", 0));
        tracer.addTrace(new Trace("Matt", "Sanni", 1800));
        tracer.addTrace(new Trace("Kenton", "Jim", 3420));

        assertEquals(Set.of("Matt", "Kristian", "Kenton", "Max", "Sanni", "Jim", "Anna"),
                tracer.contactTrace("Bob", 0));
    }

    @Test
    public void myTracerTestDisconnected() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("A", "B", 0));
        tracer.addTrace(new Trace("B", "C", 100));
        tracer.addTrace(new Trace("B", "D", 200));
        tracer.addTrace(new Trace("D", "C", 300));
        tracer.addTrace(new Trace("D", "E", 100));
        tracer.addTrace(new Trace("C", "E", 400));
        tracer.addTrace(new Trace("E", "F", 500));
        tracer.addTrace(new Trace("E", "G", 0));
        tracer.addTrace(new Trace("E", "G", 800));
        tracer.addTrace(new Trace("G", "H", 500));
        tracer.addTrace(new Trace("I", "J", 100));

        assertEquals(Set.of("J"),
                tracer.contactTrace("I", 0));
    }

    @Test
    public void testGetContacts() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));

        assertEquals(Set.of("Sanni", "Matt"), tracer.getContacts("Anna"));
        assertEquals(Set.of("Anna", "Kristian"), tracer.getContacts("Matt"));
        assertEquals(Set.of("Matt", "Sanni", "Kenton", "Max"), tracer.getContacts("Kristian"));
        assertEquals(Set.of("Anna", "Kristian"), tracer.getContacts("Sanni"));
        assertEquals(Set.of("Kristian"), tracer.getContacts("Max"));
        assertEquals(Set.of("Kristian"), tracer.getContacts("Kenton"));
    }

    @Test
    public void myTestGetContacts() {
        ContactTracer tracer = new ContactTracer();
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(new Trace("Anna", "Bob", 0));
        tracer.addTrace(new Trace("Matt", "Sanni", 1800));
        tracer.addTrace(new Trace("Kenton", "Jim", 3420));

        assertEquals(Set.of("Sanni", "Matt", "Bob"), tracer.getContacts("Anna"));
        assertEquals(Set.of("Anna", "Kristian", "Sanni"), tracer.getContacts("Matt"));
        assertEquals(Set.of("Matt", "Sanni", "Kenton", "Max"), tracer.getContacts("Kristian"));
        assertEquals(Set.of("Anna", "Kristian", "Matt"), tracer.getContacts("Sanni"));
        assertEquals(Set.of("Kristian"), tracer.getContacts("Max"));
        assertEquals(Set.of("Kristian", "Jim"), tracer.getContacts("Kenton"));
        assertEquals(Set.of("Kenton"), tracer.getContacts("Jim"));
        assertEquals(Set.of("Anna"), tracer.getContacts("Bob"));
    }

    @Test
    public void testGetContactTimes() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));

        assertEquals(List.of(3360, 4020), tracer.getContactTimes("Kristian", "Kenton"));
        assertEquals(List.of(3360, 4020), tracer.getContactTimes("Kenton", "Kristian"));

        assertEquals(List.of(), tracer.getContactTimes("Anna", "Kristian"));

        assertEquals(List.of(3360), tracer.getContactTimes("Kristian", "Max"));
        assertEquals(List.of(3360), tracer.getContactTimes("Max", "Kristian"));
    }

    @Test
    public void myTestGetContactTimes() {
        ContactTracer tracer = new ContactTracer();
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(new Trace("Kenton", "Kristian", 1));
        tracer.addTrace(new Trace("Kenton", "Kristian", 1));
        tracer.addTrace(new Trace("Kenton", "Kristian", -100));
        tracer.addTrace(new Trace("Anna", "Bob", 0));
        tracer.addTrace(new Trace("Matt", "Sanni", 1800));
        tracer.addTrace(new Trace("Matt", "Sanni", 1));
        tracer.addTrace(new Trace("Matt", "Sanni", 9999));
        tracer.addTrace(new Trace("Matt", "Sanni", 1000));
        tracer.addTrace(new Trace("Matt", "Sanni", -500));
        tracer.addTrace(new Trace("Kenton", "Jim", 3420));

        assertEquals(List.of(1, 3360, 4020), tracer.getContactTimes("Kristian", "Kenton"));
        assertEquals(List.of(1, 3360, 4020), tracer.getContactTimes("Kenton", "Kristian"));

        assertEquals(List.of(), tracer.getContactTimes("Anna", "Kristian"));

        assertEquals(List.of(3360), tracer.getContactTimes("Kristian", "Max"));
        assertEquals(List.of(3360), tracer.getContactTimes("Max", "Kristian"));

        assertEquals(List.of(1, 1000, 1800, 9999), tracer.getContactTimes("Matt", "Sanni"));
        assertEquals(List.of(1, 1000, 1800, 9999), tracer.getContactTimes("Sanni", "Matt"));
    }

    @Test
    public void testGetContactsAfter() {
        ContactTracer tracer = new ContactTracer();
        // we start at 100 here, but really its arbitrary.. only the relative time differences matter.
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));

        assertEquals(Set.of("Sanni", "Matt"), tracer.getContactsAfter("Anna", 0));
        // note below: inclusive of the timestamp
        assertEquals(Set.of("Sanni", "Matt"), tracer.getContactsAfter("Anna", 100));
        assertEquals(Set.of("Matt"), tracer.getContactsAfter("Anna", 101));
        assertEquals(Set.of(), tracer.getContactsAfter("Anna", 1741));
    }

    @Test
    public void myTestGetContactsAfter() {
        ContactTracer tracer = new ContactTracer();
        tracer.addTrace(new Trace("Anna", "Sanni", 100));
        tracer.addTrace(new Trace("Anna", "Matt", 1740));
        tracer.addTrace(new Trace("Matt", "Kristian", 3240));
        tracer.addTrace(new Trace("Kristian", "Sanni", 3270));
        tracer.addTrace(new Trace("Kristian", "Kenton", 3360));
        tracer.addTrace(new Trace("Kristian", "Max", 3360));
        tracer.addTrace(new Trace("Kenton", "Kristian", 4020));
        tracer.addTrace(new Trace("Kenton", "Kristian", 1));
        tracer.addTrace(new Trace("Kenton", "Kristian", 1));
        tracer.addTrace(new Trace("Kenton", "Kristian", -100));
        tracer.addTrace(new Trace("Anna", "Bob", 0));
        tracer.addTrace(new Trace("Matt", "Sanni", 1800));
        tracer.addTrace(new Trace("Matt", "Sanni", 1));
        tracer.addTrace(new Trace("Matt", "Sanni", 9999));
        tracer.addTrace(new Trace("Matt", "Sanni", 1000));
        tracer.addTrace(new Trace("Matt", "Sanni", -500));
        tracer.addTrace(new Trace("Kenton", "Jim", 3420));

        assertEquals(Set.of("Sanni", "Matt", "Bob"), tracer.getContactsAfter("Anna", 0));
        // note below: inclusive of the timestamp
        assertEquals(Set.of("Sanni", "Matt"), tracer.getContactsAfter("Anna", 100));
        assertEquals(Set.of("Matt"), tracer.getContactsAfter("Anna", 101));
        assertEquals(Set.of(), tracer.getContactsAfter("Anna", 1741));
    }

    @Test
    public void myTestSubsequentTraces() {
        List<Trace> traces = List.of(
                new Trace("Anna", "Sanni", 100),
                new Trace("Anna", "Matt", 1740),
                new Trace("Matt", "Kristian", 3240),
                new Trace("Kristian", "Sanni", 3270),
                new Trace("Kristian", "Kenton", 3360),
                new Trace("Kristian", "Max", 3360),
                new Trace("Kenton", "Kristian", 4020),
                new Trace("Anna", "Bob", 0),
                new Trace("Matt", "Sanni", 1800),
                new Trace("Kenton", "Jim", 3420)
        );

        ContactTracer tracer = new ContactTracer(traces);

        assertEquals(Set.of("Sanni", "Matt", "Bob"), tracer.getContacts("Anna"));
        assertEquals(Set.of("Anna", "Kristian", "Sanni"), tracer.getContacts("Matt"));
        assertEquals(Set.of("Matt", "Sanni", "Kenton", "Max"), tracer.getContacts("Kristian"));
        assertEquals(Set.of("Anna", "Kristian", "Matt"), tracer.getContacts("Sanni"));
        assertEquals(Set.of("Kristian"), tracer.getContacts("Max"));
        assertEquals(Set.of("Kristian", "Jim"), tracer.getContacts("Kenton"));
        assertEquals(Set.of("Kenton"), tracer.getContacts("Jim"));
        assertEquals(Set.of("Anna"), tracer.getContacts("Bob"));

        assertEquals(List.of(3360, 4020), tracer.getContactTimes("Kristian", "Kenton"));
        assertEquals(List.of(3360, 4020), tracer.getContactTimes("Kenton", "Kristian"));
        assertEquals(List.of(), tracer.getContactTimes("Anna", "Kristian"));
        assertEquals(List.of(3360), tracer.getContactTimes("Kristian", "Max"));
        assertEquals(List.of(3360), tracer.getContactTimes("Max", "Kristian"));

        assertEquals(Set.of("Sanni", "Matt", "Bob"), tracer.getContactsAfter("Anna", 0));
        // note below: inclusive of the timestamp
        assertEquals(Set.of("Sanni", "Matt"), tracer.getContactsAfter("Anna", 100));
        assertEquals(Set.of("Matt"), tracer.getContactsAfter("Anna", 101));
        assertEquals(Set.of(), tracer.getContactsAfter("Anna", 1741));

        tracer.addTrace(new Trace("Max", "Ben", 4000));
        tracer.addTrace(new Trace("Max", "Ben", 1000));

        assertEquals(Set.of("Max"), tracer.getContacts("Ben"));
        assertEquals(Set.of("Ben", "Kristian"), tracer.getContacts("Max"));

        assertEquals(List.of(1000, 4000), tracer.getContactTimes("Ben", "Max"));
        assertEquals(List.of(1000, 4000), tracer.getContactTimes("Max", "Ben"));

        assertEquals(Set.of("Ben", "Kristian"), tracer.getContactsAfter("Max", 100));
        assertEquals(Set.of("Max"), tracer.getContactsAfter("Ben", 100));
        assertEquals(Set.of("Ben", "Kristian"), tracer.getContactsAfter("Max", 2000));
        assertEquals(Set.of("Max"), tracer.getContactsAfter("Ben", 2000));
        assertEquals(Set.of(), tracer.getContactsAfter("Ben", 4001));
    }

    @Test
    public void myTestMultipleTraces() {
        List<Trace> traces = List.of(
                new Trace("Anna", "Sanni", 100),
                new Trace("Anna", "Matt", 1740),
                new Trace("Matt", "Kristian", 3240),
                new Trace("Kristian", "Sanni", 3270),
                new Trace("Kristian", "Kenton", 3360),
                new Trace("Kristian", "Max", 3360),
                new Trace("Kenton", "Kristian", 4020),
                new Trace("Anna", "Bob", 0),
                new Trace("Matt", "Sanni", 1800),
                new Trace("Kenton", "Jim", 3420)
        );

        ContactTracer tracer = new ContactTracer(traces);

        assertEquals(Set.of("Matt", "Kristian", "Kenton", "Max", "Jim", "Sanni", "Bob"),
                tracer.contactTrace("Anna", 0));

        assertEquals(Set.of("Matt", "Kristian", "Kenton", "Max", "Jim", "Sanni"),
                tracer.contactTrace("Anna", 130));

        assertEquals(Set.of("Kristian", "Kenton"),
                tracer.contactTrace("Jim", 3420));

        assertEquals(Set.of("Matt", "Kristian", "Kenton", "Max", "Jim", "Sanni", "Anna"),
                tracer.contactTrace("Bob", 0));

        assertEquals(Set.of(),
                tracer.contactTrace("Bob", 10));

        assertEquals(Set.of(),
                tracer.contactTrace("Bob", -10));

        assertEquals(Set.of("Matt", "Kristian", "Max", "Kenton", "Jim"),
                tracer.contactTrace("Sanni", 1800));

        assertEquals(Set.of("Kristian", "Kenton", "Jim", "Max"),
                tracer.contactTrace("Matt", 3240));

        assertEquals(Set.of("Sanni", "Kenton", "Jim", "Max"),
                tracer.contactTrace("Kristian", 3270));

        assertEquals(Set.of("Kristian"),
                tracer.contactTrace("Kenton", 4020));

        assertEquals(Set.of(),
                tracer.contactTrace("Kenton", 9999999));

        assertEquals(Set.of("Kristian", "Jim"),
                tracer.contactTrace("Kenton", 0));

        assertEquals(Set.of("Kristian", "Kenton"),
                tracer.contactTrace("Max", 0));

        assertEquals(Set.of(),
                tracer.contactTrace("Max", 9999));

        assertEquals(Set.of(),
                tracer.contactTrace("Max", -1));
    }

    @Test
    public void myTestMultipleTracesAfterInserts() {
        List<Trace> traces = List.of(
                new Trace("Anna", "Sanni", 100),
                new Trace("Anna", "Matt", 1740),
                new Trace("Matt", "Kristian", 3240),
                new Trace("Kristian", "Sanni", 3270),
                new Trace("Kristian", "Kenton", 3360),
                new Trace("Kristian", "Max", 3360),
                new Trace("Kenton", "Kristian", 4020),
                new Trace("Anna", "Bob", 0),
                new Trace("Matt", "Sanni", 1800),
                new Trace("Kenton", "Jim", 3420)
        );

        ContactTracer tracer = new ContactTracer(traces);

        assertEquals(Set.of("Matt", "Kristian", "Kenton", "Max", "Jim", "Sanni"),
                tracer.contactTrace("Anna", 130));

        tracer.addTrace(new Trace("Kristian", "Max", 4100));
        tracer.addTrace(new Trace("Max", "Ben", 4200));
        tracer.addTrace(new Trace("Max", "Ben", 1000));

        assertEquals(Set.of("Kristian", "Kenton", "Max", "Ben"),
                tracer.contactTrace("Jim", 3420));
    }

    @Test
    public void myDiabolicalTraceTest() {
        List<Trace> traces = List.of(
                new Trace("Max", "Kristian", 100),
                new Trace("Kristian", "Kenton", 2000),
                new Trace("Kristian", "Kenton", 1000),
                new Trace("Kristian", "Kenton", 3000),
                new Trace("Kristian", "Kenton", 4000),
                new Trace("Kristian", "Kenton", 5000),
                new Trace("Kenton", "Jim", 1060)
        );

        ContactTracer tracer = new ContactTracer(traces);

        assertEquals(Set.of("Kristian", "Kenton", "Jim"),
                tracer.contactTrace("Max", 100));

    }

    @Test
    public void myDiabolicalTraceTest2() {
        List<Trace> traces = List.of(
                new Trace("Max", "Kristian", 100),
                new Trace("Kristian", "Kenton", 2000),
                new Trace("Kristian", "Kenton", 1000),
                new Trace("Kristian", "Kenton", 3000),
                new Trace("Kristian", "Kenton", 4000),
                new Trace("Kenton", "Jim", 1000),
                new Trace("Max", "Jim", 0),
                new Trace("Bob", "Kristian", 2500)
        );

        ContactTracer tracer = new ContactTracer(traces);

        assertEquals(Set.of("Kristian", "Kenton", "Jim", "Bob"),
                tracer.contactTrace("Max", 0));

    }

}