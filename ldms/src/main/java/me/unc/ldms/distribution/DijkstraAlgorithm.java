package me.unc.ldms.distribution;

import me.unc.ldms.utils.AppConstant;

import java.util.Map;

/**
 * @author LZS
 * @version v1.0
 * @Description 迪杰斯特拉算法
 * @Date 2020/2/22 10:46
 */
public class DijkstraAlgorithm {

    //不能设置为Integer.MAX_VALUE，否则两个Integer.MAX_VALUE相加会溢出导致出现负权
    protected static int MaxValue = AppConstant.MAX_VALUE;

    /**
     * 迪杰斯特拉算法求图的最短路径
     * @param matrix 图的邻接矩阵
     * @param source 起点下标
     */
    public static String[] dijkstra(int[][] matrix, int source) {
        if (matrix == null) {
            throw new IllegalArgumentException("矩阵为空");
        }
        //最短路径长度
        int[] shortest = new int[matrix.length];
        //判断该点的最短路径是否求出
        int[] visited = new int[matrix.length];
        // 存储输出路径
        String[] path = new String[matrix.length];

        // 初始化输出路径
        for (int i = 0; i < matrix.length; i++) {
            path[i] = source + "-" + i;
        }

        //初始化源节点
        shortest[source] = 0;
        visited[source] = 1;

        for (int i = 1; i < matrix.length; i++) {
            int min = Integer.MAX_VALUE;
            int index = -1;
            for (int j = 0; j < matrix.length; j++) {
                //已经求出最短路径的节点不需要再加入计算并判断加入节点后是否存在更短路径
                if (visited[j] == 0 && matrix[source][j] < min) {
                    min = matrix[source][j];
                    index = j;
                }
            }

            //更新最短路径
            shortest[index] = min;
            visited[index] = 1;

            //更新从index跳到其它节点的较短路径
            for (int m = 0; m < matrix.length; m++) {
                if (visited[m] == 0 && matrix[source][index] + matrix[index][m] < matrix[source][m]) {
                    matrix[source][m] = matrix[source][index] + matrix[index][m];
                    path[m] = path[index] + "-" + m;
                }
            }
        }

        //打印最短路径
        for (int i = 0; i < matrix.length; i++) {
            if (i != source) {
                if (shortest[i] == MaxValue) {
                    System.out.println(source + "到" + i + "不可达");
                } else {
                    System.out.println(source + "到" + i + "的最短路径为：" + path[i] + "，最短距离是：" + shortest[i]);
                }
            }
        }

        return path;
    }

    /**
     * 生成图的邻接矩阵
     * @param data map(String, Integer)，两个地点索引之间的距离，String格式为 0:0 ，两数字代表索引
     * @param node 节点数量
     * @return 邻接矩阵
     */
    public static int[][] buildAdjacencyMatrix(Map<String, Integer> data, int node) {
        //初始化矩阵
        //权值计算
        int[][] matrix = new int[node][node];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                matrix[i][j] = DijkstraAlgorithm.MaxValue;
            }
        }

        int sum = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            sum += entry.getValue();
        }
        double iSum = sum;
        //System.out.println("总数：" + iSum);
        data.forEach((s, i) -> {
            int c = (int) Math.round((i / iSum) * 1000);
            //System.out.println(s + "权值：" + c);
            String[] sp = s.split(":");
            matrix[Integer.parseInt(sp[0])][Integer.parseInt(sp[1])] = c;
        });

        return matrix;
    }
}
