package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> ops = new AList<>();
        int M = 10000;
        Stopwatch sw;
        for(int i=0;i<8;i++){
            int N = (int)Math.pow(2, i) * 1000;
            Ns.addLast(N);
            ops.addLast(M);

            SLList<Integer> test = new SLList<>();
            for(int j=0;j<N;j++){
                test.addFirst(1);
            }

            sw = new Stopwatch();
            for(int t=0;t<M;t++)
                test.getLast();
            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
        }

        TimeSLList.printTimingTable(Ns, times, ops);
    }

}
