package com.sovon9.downtime_service;

import java.util.*;
import java.util.stream.IntStream;

public class Test {

    public static int characterReplacement(String s, int k) {

        int start=0;
        int arr[]=new int[26];
        int maxfreq=0;
        int change=0;
        int max=0;
        for(int i=0;i<s.length();i++)
        {
            arr[s.charAt(i)-'A']++;
            if(maxfreq<arr[s.charAt(i)-'A'])
            {
                maxfreq=arr[s.charAt(i)-'A'];
            }
            change=i-start+1-maxfreq;
            if(change>k)
            {
                arr[s.charAt(start)-'A']--;
                start++;
            }
            maxfreq=0;
            for(int j:arr)
            {
                if(j>maxfreq)
                {
                    maxfreq=j;
                }
            }
            if(max<i-start+1){
                max=i-start+1;
            }
        }
        return max;
    }



    public static void main(String args[])
    {
        Test test = new Test();
//        test.printFactors(36);


        System.out.println(characterReplacement("AABABBA", 1));
    }
}
