package com.example.algorithmvisualizer;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import java.util.*;

public class HelloController {

    @FXML
    private Canvas canvas;

    @FXML
    private Slider arraySlider;


    private AnimationTimer timer;
    private long lastUpdate = 0;
    private long delayNanos = 60_000_000;
    private static final long FINISHED_DELAY_NANOS = 20_000_000;
    private GraphicsContext gc;
    private List<Integer> numbers;
    int arraySize = 30;

    private final BubbleSortState bubbleState = new BubbleSortState();
    private final InsertionSortState insertionState = new InsertionSortState();
    private final QuickSortState quickSortState = new QuickSortState();


    private int finishedIndex = -1;
    private AlgorithmType currentAlgorithm = AlgorithmType.NONE;

    public enum AlgorithmType {
        NONE,
        BUBBLE_SORT,
        INSERTION_SORT,
        QUICK_SORT,
        FINISHED_ANIMATION
    }

    public static class BubbleSortState {
        int i = 0;
        int j = 0;

        public void reset() {
            this.i = 0;
            this.j = 0;
        }
    }

    public static class InsertionSortState {
        int i = 1;
        int j = 0;
        int key = 0;

        public void reset(int key) {
            this.i = 1;
            this.j = 0;
            this.key = key;
        }
    }

    public static class QuickSortState {
        int low = 0;
        int high = 0;
        int i = -1;
        int j = 0;
        int phase = 0;
        final Deque<int[]> stack = new ArrayDeque<>();

        public void reset(int size) {
            this.stack.clear();
            this.low = 0;
            this.high = 0;
            this.i = -1;
            this.j = 0;
            this.phase = 0;
            if(size > 1) {
                this.stack.push(new int[]{0, size - 1});
            }
        }
    }

    /**
     * Sets up the user interface when the application starts.
     * Prepares the canvas, fills the array with random numbers, sets up the slider,
     * and starts the main timer that runs the sorting steps and animations.
     */
    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        resetArray();

        //FOR THE SLIDER
        arraySlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isChanging) {
                if (!isChanging) {
                    arraySize = (int) Math.round(arraySlider.getValue());
                    resetArray();

                    arraySlider.setDisable(true);
                    arraySlider.valueChangingProperty().removeListener(this);
                }
            }
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long currentDelay = (currentAlgorithm == AlgorithmType.FINISHED_ANIMATION) ? FINISHED_DELAY_NANOS : delayNanos;

                if (now - lastUpdate >= currentDelay) {

                    // 1. HANDLING THE FINISHED ALGORITHM
                    if (currentAlgorithm == AlgorithmType.FINISHED_ANIMATION) {
                        if (numbers != null && finishedIndex < numbers.size()) {
                            finishedIndex++;
                            drawArrays();
                        } else {
                            timer.stop();
                            currentAlgorithm = AlgorithmType.NONE;
                        }
                    }
                    // 2. HANDLING THE ACTUAL ALGORITHM
                    else if (currentAlgorithm != AlgorithmType.NONE) {
                        boolean finished = false;

                        switch (currentAlgorithm) {
                            case BUBBLE_SORT:
                                finished = SortingUtils.stepBubbleSort(numbers, bubbleState);
                                break;
                            case INSERTION_SORT:
                                finished = SortingUtils.stepInsertionSort(numbers, insertionState);
                                break;
                            case QUICK_SORT:
                                finished = SortingUtils.stepQuickSort(numbers,quickSortState);
                                break;
                            default:
                                break;
                        }

                        drawArrays();

                        if (finished) {
                            // FINISHED SORTING, SWITCH TO FINISHED ANIMATION
                            currentAlgorithm = AlgorithmType.FINISHED_ANIMATION;
                            finishedIndex = -1;
                            resetStates();
                        }
                    }

                    lastUpdate = now;
                }
            }
        };
    }

    /**
     * Creates a new list with random numbers and resets the finish animation.
     * Draws the new bars onto the canvas.
     */
    private void resetArray() {
        Random r = new Random();
        numbers = new ArrayList<>();

        for (int i = 0; i < arraySize; ++i) {
            numbers.add(r.nextInt((int) canvas.getHeight() - 80) + 10);
        }
        finishedIndex = -1;
        drawArrays();
    }

    /**
     * Clears the canvas and redraws all bars.
     * Sets the color for each bar depending on the current state
     * (blue for normal, red for comparison, yellow for pivot, green for finished).
     */
    private void drawArrays() {
        gc.setFill(Color.web("#1a202c"));
        gc.fillRoundRect(0, 0, canvas.getWidth(), canvas.getHeight(),6,6);

        if (numbers == null || numbers.isEmpty()) return;

        double absoluteWidth = canvas.getWidth() / numbers.size();
        for (int i = 0; i < numbers.size(); ++i) {
            double columnHeight = numbers.get(i);
            double x = i * absoluteWidth;
            double y = canvas.getHeight() - columnHeight;

            Color barColor = Color.web("#38bdf8");

            // 1. COLORING THE FINISHED COLUMNS
            if (currentAlgorithm == AlgorithmType.FINISHED_ANIMATION) {
                if (i <= finishedIndex) {
                    barColor = Color.web("#10b981");
                }
            }
            // 2. COLORING DURING SORTING
            else if (currentAlgorithm != AlgorithmType.NONE) {
                boolean isHighlighted = false;
                boolean isPivot = false;

                if (currentAlgorithm == AlgorithmType.BUBBLE_SORT) {
                    isHighlighted = (i == bubbleState.j || i == bubbleState.j + 1);
                } else if (currentAlgorithm == AlgorithmType.INSERTION_SORT) {
                    isHighlighted = (i == insertionState.i || i == insertionState.j);
                } else if (currentAlgorithm == AlgorithmType.QUICK_SORT) {
                    isHighlighted = (i == quickSortState.i || i == quickSortState.j);
                    isPivot = (i == quickSortState.high);
                }

                if (isPivot) {
                    barColor = Color.web("#f59e0b");
                } else if (isHighlighted) {
                    barColor = Color.web("#ef4444");
                }
            }

            gc.setFill(barColor);
            gc.fillRoundRect(x, y, Math.max(1, absoluteWidth - 2), columnHeight,6,6);
        }
    }

    @FXML
    private void onBubbleSortClick() {
        startAlgorithm(AlgorithmType.BUBBLE_SORT);
    }

    @FXML
    private void onInsertionSortClick() {
        startAlgorithm(AlgorithmType.INSERTION_SORT);
    }

    @FXML
    private void onQuickSortClick() {
        startAlgorithm(AlgorithmType.QUICK_SORT);
    }

    /**
     * Stops any running animation, resets the counters, sets the selected algorithm,
     * and starts the timer to begin sorting.
     *
     * @param type The sorting algorithm to run.
     */
    private void startAlgorithm(AlgorithmType type) {
        timer.stop();
        resetStates();
        finishedIndex = -1;
        this.currentAlgorithm = type;
        timer.start();
    }

    /**
     * Resets the state trackers for all algorithms back to their starting values.
     * Sets up the stack for QuickSort and the first key for Insertion Sort.
     */
    private void resetStates() {
        bubbleState.reset();
        if(numbers != null && numbers.size() > 1) {
            insertionState.reset(numbers.get(1));
            quickSortState.reset(numbers.size());
        }
    }
}

