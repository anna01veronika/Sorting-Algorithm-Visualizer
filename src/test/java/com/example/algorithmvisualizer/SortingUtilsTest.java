package com.example.algorithmvisualizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortingUtilsTest {

    private List<Integer> list;
    private List<Integer> expected;

    @BeforeEach
    void setUp() {
        list = new ArrayList<>(List.of(6,4,1,5,2,3));
        expected = new ArrayList<>(List.of(1,2,3,4,5,6));
    }

    @Test
    void testStepBubbleSort() {

        runBubbleSort(list);
        assertEquals(expected,list,"The list is not sorted correctly with Bubble sort.");
    }

    @Test
    void testStepInsertionSort() {

        runInsertionSort(list);
        assertEquals(expected, list,"The list is not sorted correctly with Insertion sort.");
    }

    @Test
    void testStepQuickSort() {

        runQuickSort(list);
        assertEquals(expected, list,"The list is not sorted correctly with Quick sort.");
    }

    //--EDGE CASES--

    @Test
    void testEmptyList() {
        List<Integer> emptyList = new ArrayList<>();
        runBubbleSort(emptyList);
        assertTrue(emptyList.isEmpty());

        runInsertionSort(emptyList);
        assertTrue(emptyList.isEmpty());

        runQuickSort(emptyList);
        assertTrue(emptyList.isEmpty());
    }

    @Test
    void testSingleElementList() {
        List<Integer> oneElementList = new ArrayList<>(List.of(42));

        runBubbleSort(oneElementList);
        assertEquals(List.of(42),oneElementList,"The list is not sorted correctly with Bubble sort.");

        runInsertionSort(oneElementList);
        assertEquals(List.of(42),oneElementList,"The list is not sorted correctly with Bubble sort.");

        runQuickSort(oneElementList);
        assertEquals(List.of(42),oneElementList,"The list is not sorted correctly with Bubble sort.");
    }

    @Test
    void testNegativesInTheList() {
        List<Integer> expected = new ArrayList<>(List.of(-3,-2,-1,0,1,2,3));

        List<Integer> bList = new ArrayList<>(List.of(-3,2,-1,0,3,-2,1));
        runBubbleSort(bList);
        assertEquals(expected, bList, "The list is not sorted correctly with Bubble sort.");

        List<Integer> iList = new ArrayList<>(List.of(-3,2,-1,0,3,-2,1));
        runInsertionSort(iList);
        assertEquals(expected, iList, "The list is not sorted correctly with Insertion sort.");

        List<Integer> qList = new ArrayList<>(List.of(-3,2,-1,0,3,-2,1));
        runQuickSort(qList);
        assertEquals(expected, qList, "The list is not sorted correctly with Quick sort.");
    }

    // -- HELPING METHODS--

    private void runBubbleSort(List<Integer> li){
        HelloController.BubbleSortState state = new HelloController.BubbleSortState();
        boolean finished = false;

        while(!finished) {
            finished = SortingUtils.stepBubbleSort(li, state);
        }
    }

    private void runInsertionSort(List<Integer> li){
        HelloController.InsertionSortState state = new HelloController.InsertionSortState();

        if(li.size() <= 1) state.reset(0);

        else{
            state.reset(li.get(1));
        }

        boolean finished = false;

        while(!finished) {
            finished = SortingUtils.stepInsertionSort(li,state);
        }
    }

    private void runQuickSort(List<Integer> li){
        HelloController.QuickSortState state = new HelloController.QuickSortState();
        state.reset(li.size());
        boolean finished = false;

        while(!finished) {
            finished = SortingUtils.stepQuickSort(li,state);
        }
    }
}