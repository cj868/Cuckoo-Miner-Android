package com.osys.android.cuckoominer;

public class Solution {

    public byte[] header;
    public String solution = "0RRR";
    public int easipct = 50;
    public int nthreads = 1;
    public int maxsols = 8;

    public Solution(byte[] headerIn){

        header = headerIn;
    }
}
