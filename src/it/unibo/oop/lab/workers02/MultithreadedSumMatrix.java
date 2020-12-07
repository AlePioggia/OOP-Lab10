package it.unibo.oop.lab.workers02;

import java.util.*;

public class MultithreadedSumMatrix implements SumMatrix{
    
    private final int nThreads;
    
    public MultithreadedSumMatrix(int nThreads) {
        this.nThreads = nThreads;
    }
    
    private static class Worker extends Thread {
        
        private final double[][] matrix;
        private final int startingRow;
        private final int nRows;
        private double sum = 0;
        
        Worker(final double[][] matrix, final int startingRow, final int nRows) {
            super();
            this.matrix = matrix;
            this.startingRow = startingRow;
            this.nRows = nRows;
        }
        
        @Override
        public void run() {
            System.out.println("From row : " + this.startingRow + 
                                 "To row : " + (this.startingRow + this.nRows));
            for (int i = this.startingRow; i < this.matrix.length && i < this.startingRow + nRows; i++) {
                for(int j = 0; j < this.matrix.length ; j++) {
                    this.sum = this.sum + this.matrix[i][j];
                }
            }
        }
        
        public double getSum() {
            return this.sum;
        }
    }
    
    
    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % this.nThreads + matrix.length / this.nThreads;
        
        final List<Worker> workers = new ArrayList<>(nThreads);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        
        for (final Worker w: workers) {
            w.start();
        }
        
        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getSum();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        
        return sum;
    }
}
