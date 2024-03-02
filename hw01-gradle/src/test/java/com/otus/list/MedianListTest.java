package com.otus.list;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MedianListTest {

    @ParameterizedTest
    @ArgumentsSource(MedianListProvider.class)
    public void test(NumericList<Integer> list, Integer expectedSize, Double expectedMedian) {
        assertEquals(expectedSize, list.size(),
                String.format("For list %s expected size is %d", list, expectedSize));
        assertEquals(expectedMedian, list.getMedian(), 0.001,
                String.format("For list %s expected median is %3.2f", list, expectedMedian));
    }

    final static class MedianListProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new MedianList<Integer>(), 0, Double.NaN),
                    Arguments.of(new MedianList<Integer>() {{
                        add(1);
                    }}, 1, 1.0),
                    Arguments.of(new MedianList<Integer>() {{
                        add(1);
                        add(2);
                    }}, 2, 1.5),
                    Arguments.of(new MedianList<Integer>() {{
                        add(1);
                        add(2);
                        add(3);
                    }}, 3, 2.0),
                    Arguments.of(new MedianList<Integer>() {{
                        add(1);
                        add(1);
                        add(1);
                    }}, 3, 1.0),
                    Arguments.of(new MedianList<Integer>() {{
                        add(1);
                        add(2);
                        add(2);
                    }}, 3, 2.0),
                    Arguments.of(new MedianList<Integer>() {{
                        add(1);
                        add(2);
                        add(2);
                        remove(1);
                    }}, 2, 2.0),
                    Arguments.of(new MedianList<Integer>() {{
                        add(1);
                        add(2);
                        add(3);
                        add(4);
                    }}, 4, 2.5),
                    Arguments.of(new MedianList<Integer>() {{
                        add(1);
                        remove(1);
                    }}, 0, Double.NaN),
                    Arguments.of(new MedianList<Integer>() {{
                        add(1);
                        add(1);
                        remove(1);
                    }}, 1, 1.0),
                    Arguments.of(new MedianList<Integer>() {{
                        add(3);
                        add(7);
                        add(12);
                        add(16);
                        add(19);
                    }}, 5, 12.0),
                    Arguments.of(new MedianList<Integer>() {{
                        add(19);
                        add(16);
                        add(12);
                        add(7);
                        add(3);
                    }}, 5, 12.0),
                    Arguments.of(new MedianList<Integer>() {{
                        add(4);
                        add(6);
                        add(10);
                        add(13);
                        add(18);
                        add(22);
                    }}, 6, 11.5),
                    Arguments.of(new MedianList<Integer>() {{
                        add(22);
                        add(18);
                        add(13);
                        add(10);
                        add(6);
                        add(4);
                    }}, 6, 11.5)
            );
        }
    }
}
