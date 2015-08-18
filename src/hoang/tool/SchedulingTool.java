package hoang.tool;

public class SchedulingTool {
	/**
	 * scheduling n independent jobs on m identical machines so as the makespan
	 * is minimized Algorithm: Longest processing time first
	 * 
	 * @param workloads
	 *            : workloads of n jobs
	 * @param m
	 *            : #machines
	 * @return: array of
	 */
	public static int[][] minMakeSpan(int[] workloads, int m) {
		RankingTool rankTool = new RankingTool();
		String[] jobNames = new String[workloads.length];
		for (int j = 0; j < jobNames.length; j++) {
			jobNames[j] = j + "";
		}
		WeightedElement[] jobs = rankTool.getTopKbyWeight(jobNames, workloads,
				workloads.length);
		int[] jobAllocations = new int[workloads.length];
		for (int j = 0; j < jobAllocations.length; j++) {
			jobAllocations[j] = -1;
		}
		int[] machineLoads = new int[m];
		int[] jobCounts = new int[m];
		for (int i = 0; i < m; i++) {
			machineLoads[i] = 0;
			jobCounts[i] = 0;
		}
		int[] nextmin = new int[m];
		for (int i = 0; i < m; i++) {
			int j = Integer.parseInt(jobs[i].name);
			jobAllocations[j] = i;
			machineLoads[i] = workloads[j];
			jobCounts[i]++;
			nextmin[i] = i - 1;
		}
		int minIndex = m - 1;

		for (int i = m; i < workloads.length; i++) {
			int j = Integer.parseInt(jobs[i].name);
			jobAllocations[j] = minIndex;
			machineLoads[minIndex] += workloads[j];
			jobCounts[minIndex]++;
			if (nextmin[minIndex] == -1)
				continue;
			if (machineLoads[minIndex] <= machineLoads[nextmin[minIndex]])
				continue;
			int preminIndex = minIndex;
			minIndex = nextmin[minIndex];
			int prev = minIndex;
			int next = nextmin[prev];
			while (machineLoads[prev] < machineLoads[preminIndex] && next != -1) {
				prev = next;
				next = nextmin[next];
			}
			nextmin[prev] = preminIndex;
			nextmin[preminIndex] = next;
		}
		int[][] schedules = new int[m][];
		for (int i = 0; i < m; i++) {
			System.out.println("machine " + i + " workload " + machineLoads[i]
					+ " job " + jobCounts[i]);
			schedules[i] = new int[jobCounts[i]];
			jobCounts[i] = 0;			
		}

		for (int j = 0; j < workloads.length; j++) {
			int mIndex = jobAllocations[j];
			schedules[mIndex][jobCounts[mIndex]] = j;
			jobCounts[mIndex]++;
		}
		return schedules;
	}
}
