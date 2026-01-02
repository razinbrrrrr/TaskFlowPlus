public class RiskCalculator {

    public static double productivityScore(int completed, int total) {
        if (total == 0) return 0;
        return (completed * 100.0) / total;
    }

    public static double consistencyScore(int activeDays, int totalDays) {
        if (totalDays == 0) return 0;
        return (activeDays * 100.0) / totalDays;
    }

    public static double delayIndex(double avgDelay) {
        return Math.max(0, avgDelay);
    }

    public static double focusScore(int highDone, int highTotal) {
        if (highTotal == 0) return 0;
        return (highDone * 100.0) / highTotal;
    }

    public static double overallRisk(
            double productivity,
            double consistency,
            double delay,
            double focus
    ) {
        return
            (100 - productivity) * 0.30 +
            (100 - consistency) * 0.25 +
            delay * 0.25 +
            (100 - focus) * 0.20;
    }
}
