package com.osys.android.cuckoominer;

/**
 * This application is based on the SimpleMiner.java by John Tromp.
 *
 * Cuckoo Cycle, a memory-hard proof-of-work
 * Copyright (c) 2013-2016 John Tromp
 */

import android.util.Log;

public class SimpleMiner implements Runnable {

    int id;
    CuckooSolve solve;
    int nthreads;
    int maxsols;
    int easipct;
    byte[] header;
    Solution soln;
    Thread mThreadThis;

    public SimpleMiner(int i, Solution solIn) {

        Log.i("CUCKOOMINER", "SimpleMiner");

        id = i;
        soln = solIn;
        nthreads = solIn.nthreads;
        maxsols = solIn.maxsols;
        easipct = solIn.easipct;
        header = solIn.header;

    }

    public void run() {

        Log.i("CUCKOOMINER", "SimpleMiner - Run");

        solve = new CuckooSolve(header, (int)(easipct * (long)Cuckoo.NNODES / 100), maxsols, nthreads);
        int[] cuckoo = solve.cuckoo;
        int[] us = new int[CuckooSolve.MAXPATHLEN], vs = new int[CuckooSolve.MAXPATHLEN];
        for (int nonce = id; nonce < solve.easiness; nonce += solve.nthreads) {
            int u = cuckoo[us[0] = (int)solve.graph.sipnode(nonce,0)];
            int v = cuckoo[vs[0] = (int)(Cuckoo.NEDGES + solve.graph.sipnode(nonce,1))];
            if (u == vs[0] || v == us[0])
                continue; // ignore duplicate edges
            int nu = solve.path(u, us), nv = solve.path(v, vs);
            if (us[nu] == vs[nv]) {
                int min = nu < nv ? nu : nv;
                for (nu -= min, nv -= min; us[nu] != vs[nv]; nu++, nv++) ;
                int len = nu + nv + 1;

                if(len == 42) {
                    Log.i("CUCKOOMINER", " " + len + "-cycle found at " + id + ":" + (int)(nonce*100L/solve.easiness) + "%" + " for header " + solve.b);
                    soln.solution = "1";

                }
                SolutionManager.getInstance().handleState(soln, id);
                if (len == Cuckoo.PROOFSIZE && solve.nsols < solve.sols.length)
                    solve.solution(us, nu, vs, nv);
                continue;
            }
            if (nu < nv) {
                while (nu-- != 0)
                    cuckoo[us[nu+1]] = us[nu];
                cuckoo[us[0]] = vs[0];
            } else {
                while (nv-- != 0)
                    cuckoo[vs[nv+1]] = vs[nv];
                cuckoo[vs[0]] = us[0];
            }
        }
        Thread.currentThread().interrupt();
    }




}
