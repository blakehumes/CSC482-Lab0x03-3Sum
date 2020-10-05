package com.company;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {

        RunTimeTests(Integer.MAX_VALUE);


        // CODE FOR TESTING ALGORITHM CORRECTNESS
        /*System.out.println();
        for(int i = 0; i < 10; i++) {
            int[] ok = GenerateTestList(8, -10, 10);
            System.out.format("Initial List: ");
            for (int e : ok) {
                System.out.format("%d ", e);
            }
            System.out.format("\nBrute Force Sum3: ");
            int[] yeah = RemoveDuplicates(ok);
            for (int e : yeah) {
                System.out.format("%d ", e);
            }
            int[] yeah = FastestSum3(ok, ok.length);
            System.out.format("%d ", yeah[0]);
            if(yeah[0] == 1){
                for(int j = 1; j < 4; j++) System.out.format("%d ", yeah[j]);
            }
            System.out.format("\n");
        }*/
    }
    public static void RunTimeTests(int nMax){
        // Formatting the table
        System.out.format("%12s %12s %12s %18s %12s %12s %18s %12s %12s %18s\n",
                "", "Brute 3sum", "", "", "Faster 3sum",
                "", "", "Fastest 3sum", "", "");
        System.out.format("%12s %12s %12s %18s %12s %12s %18s %12s %12s %18s\n",
                "N", "Time", "2x Ratio", "Pred 2x Ratio", "Time", "2x Ratio",
                "Pred 2x Ratio","Time", "2x Ratio", "Pred 2x Ratio");

        // Declaring the variables that track the times and ratios
        long StartSort = 0, EndSort = 0;
        long[] Previous = new long[3];
        long[] Current = new long[3];
        float[] DoubleRatio = new float[3];
        double[] PredDoublRatio = new double[3];
        PredDoublRatio[0] = 8; // Predicted Doubling Ratio for N^3
        PredDoublRatio[1] = 0; // Predicted Doubling Ratio for (N^2)*logN, varies based on N
        PredDoublRatio[2] = 4; // Predicted Doubling Ratio for N^2

        // Fill arrays with zeroes to ensure no random values
        Arrays.fill(Previous, 0);
        Arrays.fill(Current, 0);

        // Loop through tests, doubling N each time
        for(int N = 4; N <= nMax; N += N){
            int[] testArray = GenerateTestList(N,Integer.MIN_VALUE,Integer.MAX_VALUE);

            // BruteForce test and timestamps
            StartSort = getCpuTime();
            BruteForceSum3(testArray, testArray.length);
            EndSort = getCpuTime();
            Current[0] = (EndSort - StartSort)/ 1000;

            // Faster test and timestamps
            StartSort = getCpuTime();
            FasterSum3(testArray, testArray.length);
            EndSort = getCpuTime();
            Current[1] = (EndSort - StartSort)/ 1000;

            // Fastest test and timestamps
            StartSort = getCpuTime();
            FastestSum3(testArray, testArray.length);
            EndSort = getCpuTime();
            Current[2] = (EndSort - StartSort)/ 1000;

            // If N <= 1024, repeat the test 9 more times to get an average of 10 tests
            if(N <= 1024){
                for(int k = 0; k < 9; k++){
                    testArray = GenerateTestList(N,Integer.MIN_VALUE,Integer.MAX_VALUE);

                    StartSort = getCpuTime();
                    BruteForceSum3(testArray, testArray.length);
                    EndSort = getCpuTime();
                    Current[0] += (EndSort - StartSort)/ 1000;

                    StartSort = getCpuTime();
                    FasterSum3(testArray, testArray.length);
                    EndSort = getCpuTime();
                    Current[1] += (EndSort - StartSort)/ 1000;

                    StartSort = getCpuTime();
                    FastestSum3(testArray, testArray.length);
                    EndSort = getCpuTime();
                    Current[2] += (EndSort - StartSort)/ 1000;
                }
                Current[0] = Current[0] / 10;
                Current[1] = Current[1] / 10;
                Current[2] = Current[2] / 10;
            }

            // If loop is not on the first iteration, compute doubling ratios and the expected ratio for N2logN
            if(N > 4) {
                DoubleRatio[0] = (float) Current[0] / Previous[0];
                DoubleRatio[1] = (float) Current[1] / Previous[1];
                DoubleRatio[2] = (float) Current[2] / Previous[2];
                PredDoublRatio[1] = Math.pow(N,2) * Math.log(N)/Math.log(2) /
                        (Math.pow(N/2,2) * Math.log(N/2)/Math.log(2));
            }

            // Formatted table results
            System.out.format("%12s %12s %12.1f %18.1f %12s %12.1f %18.1f %12s %12.1f %18s\n",
                    N, Current[0], DoubleRatio[0], PredDoublRatio[0], Current[1], DoubleRatio[1],
                    PredDoublRatio[1], Current[2], DoubleRatio[2], PredDoublRatio[2]);

            // Assign current times to the previous array (used to calculate doubling ratios)
            Previous[0] = Current[0];
            Previous[1] = Current[1];
            Previous[2] = Current[2];
        }

    }

    public static  int[] FastestSum3(int[] intArray, int length){
        int[] results = new int[4]; // 0 index is for T/F a 3sum exists. 1-3 indices are for the 3sum.
        results[0] = 0; // Marking the 3sum as false.
        int a, b, c; // Variables to test for 3sum

        // Create new array with sorted, distinct values
        int[] testArray = MergeSort(RemoveDuplicates(intArray));
        length = testArray.length; // New length of array after removing duplicates

        // Iterate through the sorted array to get the 1st value for the sum
        // The 2nd value starts at i + 1 and its index is incremented if the sum of the 3 is less than zero
        // The 3rd value starts at the end of the array and its index is decremented if the sum of the 3 is greater than zero
        // The 2nd and 3rd values' indices are incremented and decremented until j = k or the sum of the 3 equals zero
        for(int i = 0; i < length - 2; i++){

            int j = i + 1; // Index of 2nd value
            int k = length - 1; // Index of 3rd value

            while(j < k){

                // Increment index of 2nd value if sum is less than zero
                if(testArray[i] + testArray[j] + testArray[k] < 0){
                    j++;
                }
                // Decrement index of 3rd value if sum is greater than zero
                else if(testArray[i] + testArray[j] + testArray[k] > 0){
                    k--;
                }
                // Return results array if sum is equal to zero
                else if(testArray[i] + testArray[j] + testArray[k] == 0){
                    results[0] = 1;
                    results[1] = testArray[i];
                    results[2] = testArray[j];
                    results[3] = testArray[k];
                    return results;
                }
            }
        }
        return results; // return "false" results array
    }
    public static int[] FasterSum3(int[] intArray, int length){
        int[] results = new int[4]; // 0 index is for T/F a 3sum exists. 1-3 indices are for the 3sum.
        results[0] = 0; // Marking the 3sum as false.
        int a, b, c; // Variables to test for 3sum

        // Create new array with sorted, distinct values
        int[] testArray = MergeSort(RemoveDuplicates(intArray));
        length = testArray.length; // New length of array after removing duplicates

        for(int i = 0; i < length - 2; i++){
            for(int j = i + 1; j < length - 1; j++){
                int begin = j + 1; // Lower bound of array for this inner loop starts at j + 1
                int end = length - 1; // Upper bound for inner loop
                int k = end - (end - begin) / 2; // Middle of upper and lower bound
                while(begin <= end){
                    // Assign the 3 test variables to a, b, and c
                    a = testArray[i];
                    b = testArray[j];
                    c = testArray[k];

                    // If the 3 variables equal 0,  return them
                    if(a + b + c == 0) {
                        results[0] = 1; // Flip T/F variable to T
                        results[1] = a;
                        results[2] = b;
                        results[3] = c;
                        return results;
                    }
                    // If sum is less than zero, move lower bound to k + 1
                    else if(a + b + c < 0){
                        begin = k + 1;
                    }
                    // If sum is greater than zero, move upper bound to k - 1
                    else if(a + b + c > 0){
                        end = k - 1;
                    }
                    // Assign k/"middle" variable to the middle of the new upper and lower bounds
                    k = end - (end - begin) / 2;

                }

            }
        }

        return results;
    }
    public static int[] RemoveDuplicates(int[] intArray){
        // Hash Set only tracks distinct values
        HashSet<Integer> intSet = new HashSet<Integer>();

        // Load hash set with values from array
        for(int e : intArray){
            intSet.add(e);
        }
        int[] nodupes = new int[intSet.size()];
        int i = 0;

        // Load new array with values from hash set
        for(int e : intSet){
            nodupes[i] = e;
            i++;
        }
        return nodupes;
    }
    public static int[] MergeSort(int[] intArray){
        int n = intArray.length;

        // Arrays of less than 2 elements don't need sorted. Return array as is.
        if(n < 2)
            return intArray;

        int mid = n / 2; // Mid point of array
        int[] left = new int[mid]; // Create left array for 1st half of intArray
        int[] right = new int[n - mid]; // Create right array for 2nd half of intArray

        // Fill left and right array with 1st and 2nd half of String Array, respectively.
        for(int i = 0; i < mid; i++){
            left[i] = intArray[i];
        }
        for(int j = 0; j < n - mid; j++){
            right[j] = intArray[mid + j];
        }

        // Let the recursion commence!
        MergeSort(left);
        MergeSort(right);

        // Merge left and right array into intArray
        return Merge(intArray, left, right);

    }

    public static int[] Merge(int[] intArray, int[] left, int[] right){
        int l = left.length;
        int r = right.length;
        int i = 0; //index for left array
        int j = 0; //index for right array
        int k = 0; //index for main array

        // Compare right and left arrays until at least one array is empty
        while(i < l && j < r){
            // Sorts the lesser of the current elements of left and right arrays into intArray
            // If current element of left array is lesser or equal to current element in right array, sort left element into intArray and increment index values
            // Else sort right element into intArray and increment index values
            if(left[i] <= right[j]){
                intArray[k++] = left[i++];
                //Found the post increment trick here: https://www.baeldung.com/java-merge-sort
            }
            else{
                intArray[k++] = right[j++];
            }
        }

        // After at least 1 array is empty, copy remaining elements of the other array into intArray
        while(i < l){
            intArray[k++] = left[i++];
        }
        while(j < r){
            intArray[k++] = right[j++];
        }

        return intArray;
    }
    public static int[] BruteForceSum3(int[] intArray, int length){
        int[] results = new int[4]; // 0 index is for T/F a 3sum exists. 1-3 indices are for the 3sum.
        results[0] = 0; // Marking the 3sum as false.
        int a, b, c; // Variables to test for 3sum

        for(int i = 0; i < length - 2; i++){
            for(int j = i + 1; j < length - 1; j++){
                for(int k = j + 1; k < length; k++){
                    // Assign the 3 test variables to a, b, and c
                    a = intArray[i];
                    b = intArray[j];
                    c = intArray[k];

                    // If the 3 variables equal 0 and are distinct, then return them if true
                    if(a + b + c == 0 && a != b && b != c && c != a){
                        results[0] = 1; // Flip T/F variable to T
                        results[1] = a;
                        results[2] = b;
                        results[3] = c;
                        return results;
                    }
                }
            }
        }
        // Return "false" 0 index and empty 1-3 indices if the 3 variables never equal 0
        return results;
    }
    public static int[] GenerateTestList(int N, int minV, int maxV){

        int[] intArray = new int[N];

        //Loops through  array and fills with random int
        for(int i = 0; i < N; i++){
                    //Fill array with random char between minV and maxV
                    //Used top comment for random number generator https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
                    intArray[i] =  ThreadLocalRandom.current().nextInt(minV, maxV);
        }
        return intArray;
    }
    /** Get CPU time in nanoseconds since the program(thread) started. */
    /** from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/
    public static long getCpuTime( ) {

        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

        return bean.isCurrentThreadCpuTimeSupported( ) ?

                bean.getCurrentThreadCpuTime( ) : 0L;

    }
}
