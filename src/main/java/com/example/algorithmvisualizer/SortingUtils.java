package com.example.algorithmvisualizer;

import java.util.List;

public class SortingUtils {


    /**
            * Does one step of Bubble Sort.
            * Compares two neighboring numbers, swaps them if needed, and moves to the next pair.
            *
            * @param array The list of numbers to sort.
            * @param state Stores the current loop positions (i, j).
            * @return True if the sort is finished, false if it is still working.
            */
    public static boolean stepBubbleSort(List<Integer> array, HelloController.BubbleSortState state) {
        if (state.i < array.size() - 1) {
            if (state.j < array.size() - state.i - 1) {
                if (array.get(state.j) > array.get(state.j + 1)) {
                    int tmp = array.get(state.j);
                    array.set(state.j, array.get(state.j + 1));
                    array.set(state.j + 1, tmp);
                }
                ++state.j;
            } else {
                state.j = 0;
                ++state.i;
            }
            return false;
        }
        return true;
    }


    /**
     * Does one step of Insertion Sort.
     * Shifts larger numbers to the right and places the current number into its correct spot.
     *
     * @param array The list of numbers to sort.
     * @param state Stores the loop counters and the number being inserted.
     * @return True if the sort is finished, false if it is still working.
     */
    public static boolean stepInsertionSort(List<Integer> array, HelloController.InsertionSortState state) {
        if (state.i < array.size()) {
            if (state.j >= 0 && array.get(state.j) > state.key) {
                array.set(state.j + 1, array.get(state.j));
                --state.j;
                return false;
            }
            array.set(state.j + 1, state.key);
            ++state.i;
            if (state.i < array.size()) {
                state.j = state.i - 1;
                state.key = array.get(state.i);
            }
            return false;
        }
        return true;
    }

    /**
     * Does one step of QuickSort.
     * Uses a stack instead of recursion and runs in 3 steps: gets a range,
     * moves elements around the pivot, and places the pivot in its final spot.
     *
     * @param array The list of numbers to sort.
     * @param state Stores the working ranges, counters, and the current phase.
     * @return True if all numbers are sorted, false if it is still working.
     */
    public static boolean stepQuickSort(List<Integer> array, HelloController.QuickSortState state) {
        if (state.phase == 0) {
            if (state.stack.isEmpty()) {
                return true;
            }
            int[] range = state.stack.pop();
            int low = range[0];
            int high = range[1];

            if (low < high) {
                state.low = low;
                state.high = high;
                state.i = low - 1;
                state.j = low;
                state.phase = 1;
            }
            return false;
        }

        int pivot = array.get(state.high);

        if (state.phase == 1) {
            if (state.j < state.high) {
                if (array.get(state.j) < pivot) {
                    ++state.i;
                    int temp = array.get(state.i);
                    array.set(state.i, array.get(state.j));
                    array.set(state.j, temp);
                }
                ++state.j;
                return false;
            } else {
                state.phase = 2;
            }
        }

        if (state.phase == 2) {
            int pivotIndex = state.i + 1;
            int temp = array.get(pivotIndex);
            array.set(pivotIndex, array.get(state.high));
            array.set(state.high, temp);

            if (pivotIndex + 1 < state.high) {
                state.stack.push(new int[]{pivotIndex + 1, state.high});
            }
            if (state.low < pivotIndex - 1) {
                state.stack.push(new int[]{state.low, pivotIndex - 1});
            }

            state.phase = 0;
            return false;
        }

        return false;
    }
}
