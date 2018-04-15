package com.osys.android.cuckoominer;

/**
 * This application is based on the SimpleMiner.java by John Tromp.
 *
 * Cuckoo Cycle, a memory-hard proof-of-work
 * Copyright (c) 2013-2016 John Tromp
 */

public class Edge {

    int u;
    int v;

    public Edge(int x, int y) {
        u = x;
        v = y;
    }

    public int hashCode() {
        return (int)(u^v);
    }

    public boolean equals(Object o) {
        Edge f = (Edge)o;
        return u == f.u && v == f.v;
    }
}
