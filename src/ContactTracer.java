import java.util.*;

public class ContactTracer {

    // Map of names to their associated vertex.
    private final HashMap<String, Vertex> vertices;

    /**
     * Initialises an empty ContactTracer with no populated contact traces.
     */
    public ContactTracer() {
        this.vertices = new HashMap<>();
    }

    /**
     * Initialises the ContactTracer and populates the internal data structures
     * with the given list of contract traces.
     *
     * @param traces to populate with
     * @require traces != null
     */
    public ContactTracer(List<Trace> traces) {
        this();
        if (traces != null) {
            for (Trace currTrace : traces) {
                addTrace(currTrace);
            }
        }
    }

    /**
     * Adds a new contact trace.
     * <p>
     * If a contact trace involving the same two people at the exact same time
     * is already stored, do nothing.
     *
     * @param trace to add
     * @require trace != null
     */
    public void addTrace(Trace trace) {
        if (trace != null) {
            String person1 = trace.getPerson1();
            String person2 = trace.getPerson2();
            int time = trace.getTime();
            if (person1 != null && person2 != null && time >= 0 &&
                    !person1.equals(person2)) {
                Vertex start = insertVertex(person1);
                Vertex end = insertVertex(person2);
                insertEdge(start, end, time);
            }
        }
    }

    /**
     * Gets a list of times that person1 and person2 have come into direct
     * contact (as per the tracing data).
     * <p>
     * If the two people haven't come into contact before, an empty list is
     * returned.
     * <p>
     * Otherwise the list should be sorted in ascending order.
     *
     * @param person1
     * @param person2
     * @return a list of contact times, in ascending order.
     * @require person1 != null && person2 != null
     */
    public List<Integer> getContactTimes(String person1, String person2) {
        Vertex start = getVertex(person1);
        Vertex end = getVertex(person2);
        if (start != null && end != null) {
            Edge contact = getEdge(start, end);
            if (contact != null) {
                // Each edge contains a sorted TreeSet of times that contact
                // occurred between the two vertices.
                TreeSet<Integer> times = contact.getContactTimes();
                if (times != null && !times.isEmpty()) {
                    return List.copyOf(times);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Gets all the people that the given person has been in direct contact
     * with over the entire history of the tracing dataset.
     *
     * @param person to list direct contacts of
     * @return set of the person's direct contacts
     */
    public Set<String> getContacts(String person) {
        Vertex current = getVertex(person);
        if (current != null) {
            return current.getEdges().keySet();
        }
        return new HashSet<>();
    }

    /**
     * Gets all the people that the given person has been in direct contact
     * with at OR after the given timestamp (i.e. inclusive).
     *
     * @param person    to list direct contacts of
     * @param timestamp to filter contacts being at or after
     * @return set of the person's direct contacts at or after the timestamp
     */
    public Set<String> getContactsAfter(String person, int timestamp) {
        HashSet<String> contactsAfter = new HashSet<>();
        if (person != null && timestamp >= 0) {
            Vertex current = getVertex(person);
            if (current != null) {
                HashMap<String, Edge> edges = current.getEdges();
                // for each edge of the current vertex.
                for (Map.Entry<String, Edge> next : edges.entrySet()) {
                    // check each contact time stored in that edge.
                    for (Integer time : next.getValue().getContactTimes()) {
                        if (time >= timestamp) {
                            contactsAfter.add(next.getKey());
                            break;
                        }
                    }
                }
            }
        }
        return contactsAfter;
    }

    /**
     * Initiates a contact trace starting with the given person, who
     * became contagious at timeOfContagion.
     * <p>
     * Note that the return set shouldn't include the original person the trace
     * started from.
     *
     * @param person          to start contact tracing from
     * @param timeOfContagion the exact time person became contagious
     * @return set of people who may have contracted the disease, originating
     * from person
     */
    public Set<String> contactTrace(String person, int timeOfContagion) {
        // Map name of infected person to time at which they are contagious.
        HashMap<String, Integer> infected = new HashMap<>();
        // Maintain list of edges that have already been checked.
        HashSet<Edge> checked = new HashSet<>();
        if (person != null && timeOfContagion >= 0) {
            Vertex current = getVertex(person);
            if (current != null) {
                infected.put(person, timeOfContagion);
                trace(infected, checked, current);
                infected.remove(person);
            }
        }
        return infected.keySet();
    }

    /**
     * Recursively trace the infected path through the graph.
     * Checks infected vertices' edges in ascending order of time.
     *
     * @param infected All infected people found so far, mapped to the time at
     *                 which they became contagious themselves.
     * @param checked  Set of all edges that have been checked so far in our
     *                 traversal.
     * @param source   The vertex to start tracing from.
     */
    private void trace(HashMap<String, Integer> infected,
                       HashSet<Edge> checked, Vertex source) {
        if (source == null) {
            return;
        }
        // Place source vertex edges in min-heap priority queue.
        HashMap<String, Edge> edges = source.getEdges();
        PriorityQueue<Edge> edgeHeap = new PriorityQueue<>(edges.values());
        // Check every edge of the given source vertex, in ascending order.
        while (!edgeHeap.isEmpty()) {
            Edge edge = edgeHeap.poll();
            if (!checked.contains(edge)) {
                checked.add(edge);
                Vertex current = opposite(source, edge);
                if (current == null) {
                    return;
                }
                String currName = current.getName();
                // Check each contact time against the time at which source
                // vertex became contagious (in ascending order of time).
                for (Integer time : edge.getContactTimes()) {
                    // If this edge's contact time exceeds/matches source
                    // vertex contagious time, log the infection and continue
                    // to trace from current vertex.
                    if (time >= infected.get(source.name)) {
                        infected.put(currName, (time + 60));
                        trace(infected, checked, current);
                        // If we found an infection time, we don't need
                        // to check any higher times.
                        break;
                    }
                }
            }
        }
    }

    /**
     * Return the vertex at the other end of the given edge.
     *
     * @param source The start vertex of the edge.
     * @param edge   The edge to find the other end of.
     * @return The vertex opposite the given vertex.
     */
    private Vertex opposite(Vertex source, Edge edge) {
        Vertex[] ends = edge.getVertices();
        if (ends[0] == source) {
            return ends[1];
        } else if (ends[1] == source) {
            return ends[0];
        } else {
            return null;
        }
    }

    /**
     * Create and insert new vertex with the given name.
     * If a vertex with this name already exists, return it instead.
     *
     * @param name The name of the person to create the vertex with.
     * @return The vertex associated with this name.
     */
    private Vertex insertVertex(String name) {
        if (name == null) {
            return null;
        }
        Vertex currVert;
        if (!vertices.containsKey(name)) {
            currVert = new Vertex(name);
            vertices.put(name, currVert);
        } else {
            currVert = vertices.get(name);
        }
        return currVert;
    }

    /**
     * Create and insert an edge between the given vertices.
     *
     * @param start The start vertex of the edge.
     * @param end   The end vertex of the edge.
     * @param time  The time at which the two vertices met.
     */
    private void insertEdge(Vertex start, Vertex end, int time) {
        if (start == null || end == null || time < 0) {
            return;
        }
        Edge existingEdge = getEdge(start, end);
        if (existingEdge == null) {
            Edge newEdge = new Edge(start, end, time);
            start.insertEdge(newEdge, end.getName());
            end.insertEdge(newEdge, start.getName());
        } else {
            existingEdge.addContactTime(time);
        }
    }

    /**
     * Return the edge that joins the given start and end vertices.
     *
     * @param start The start vertex of the edge.
     * @param end   The end vertex of the edge.
     * @return The edge if it exists, else null.
     */
    private Edge getEdge(Vertex start, Vertex end) {
        return start.getEdge(end.getName());
    }

    /**
     * Return the vertex with the given name.
     *
     * @param name The name of the vertex to retrieve.
     * @return The vertex, or null if it does not exist.
     */
    private Vertex getVertex(String name) {
        return vertices.get(name);
    }

    /**
     * Vertex class for use in a graph data structure.
     */
    private class Vertex {
        // The name of this vertex, to be used as a key in the graph's
        // vertex hashmap.
        String name;
        // Edges starting from this vertex and ending at another. The key
        // is the name of the vertex opposite.
        HashMap<String, Edge> edges;

        /**
         * Create a Vertex with the given name.
         *
         * @param name This person's name.
         */
        private Vertex(String name) {
            this.name = name;
            this.edges = new HashMap<>();
        }

        /**
         * @return this vertices' edges.
         */
        private HashMap<String, Edge> getEdges() {
            return this.edges;
        }

        /**
         * Return the edges whose end is the target vertex.
         *
         * @param target The other end of the desired edge.
         * @return The edge that has the target vertex.
         */
        private Edge getEdge(String target) {
            return this.edges.get(target);
        }

        /**
         * @return this vertices' name.
         */
        private String getName() {
            return this.name;
        }

        /**
         * Insert the given edge into the edges map.
         *
         * @param newEdge The newly created edge to insert.
         * @param toName  The name of the vertex at the opposite end of the
         *                edge.
         */
        private void insertEdge(Edge newEdge, String toName) {
            if (newEdge != null && toName != null) {
                this.edges.put(toName, newEdge);
            }
        }

    }

    /**
     * Edge class connecting two vertices in a graph, with the weight being the
     * given times of contact between the two vertices.
     */
    private class Edge implements Comparable<Edge> {
        // The vertices at each end of this edge.
        Vertex start;
        Vertex end;
        // The time(s) at which the two vertices were in contact.
        TreeSet<Integer> contactTimes;

        /**
         * Create an edge with the given start and end vertices.
         *
         * @param start The start vertex.
         * @param end   The end vertex.
         */
        private Edge(Vertex start, Vertex end, int contactTime) {
            this.start = start;
            this.end = end;
            this.contactTimes = new TreeSet<>();
            this.contactTimes.add(contactTime);
        }

        /**
         * @return This edge's start and end vertices, where index 0
         * is the start vertex and index 1 is the end vertex.
         */
        private Vertex[] getVertices() {
            Vertex[] vertices = new Vertex[2];
            vertices[0] = start;
            vertices[1] = end;
            return vertices;
        }

        /**
         * @return The contact time between this edges' vertices.
         */
        private TreeSet<Integer> getContactTimes() {
            return contactTimes;
        }

        /**
         * @return The earliest time of contact for this edge.
         */
        private int getFirstContact() {
            return this.contactTimes.first();
        }

        /**
         * Add the given time of contact to the set of contact times for
         * this edge.
         *
         * @param time The contact time to add.
         */
        private void addContactTime(int time) {
            contactTimes.add(time);
        }

        /**
         * Compare this edge against another to find if one is greater, less
         * than or equal to the other.
         *
         * @param other The edge to compare this one against.
         * @return 0 if they are equal, less than 0 if this edge is less than
         * the other, greater than 0 if this edge is greater than the other.
         */
        @Override
        public int compareTo(Edge other) {
            return Integer.compare(getFirstContact(), other.getFirstContact());
        }
    }

}
