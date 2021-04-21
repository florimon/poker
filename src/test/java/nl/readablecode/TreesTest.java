package nl.readablecode;

import java.util.function.UnaryOperator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static nl.readablecode.TreesTest.MyClass.al;
import static nl.readablecode.TreesTest.MyClass.el;

public class TreesTest {

    @ToString(of = "i")
    @Getter
    @RequiredArgsConstructor
    static class MyClass {
        final int i;
        final List<MyClass> children;
        final boolean ann;

        static MyClass el(int i, MyClass... children) {
            return new MyClass(i, new ArrayList<>(Arrays.asList(children)), false);
        }

        static MyClass al(int i, MyClass... children) {
            return new MyClass(i, new ArrayList<>(Arrays.asList(children)), true);
        }
    }

    @Test
    public void testIt() {
        List<MyClass> list = new ArrayList<>(asList(el(1, el(11, el(111), el(112)), al(12)), al(2, el(22, el(221), el(222))),
                                                    el(3, el(33, el(331), el(332)))));

        List<Integer> integers = new ArrayList<>();
        breadthFirst(list, MyClass::getChildren, myclass -> !integers.add(myclass.i));
        System.out.println(integers);

        MyClass myClass1 = breadthFirst(list, MyClass::getChildren, myClass -> myClass.ann);
        System.out.println(myClass1);

        List<Integer> integers2 = new ArrayList<>();
        depthFirst(list, MyClass::getChildren, myclass -> !integers2.add(myclass.i));
        System.out.println(integers2);

        MyClass myClass2 = depthFirst(list, MyClass::getChildren, myClass -> myClass.ann);
        System.out.println(myClass2);

    }

    // class depth first, interfaces breadth first
    private <T extends Annotation> T getAnnotations(Class<?> aClass, Class<T> annotation) {
        return ofNullable(aClass.getAnnotation(annotation)).orElseGet(() ->
                ofNullable(aClass.getSuperclass()).map(c -> getAnnotations(c, annotation)).orElseGet(() ->
                        getInterfaceAnnotations(aClass.getInterfaces(), annotation)));
    }

    // depth first
    private <T extends Annotation> T getInterfaceAnnotations(Class<?> aClass, Class<T> annotation) {
        return ofNullable(aClass.getAnnotation(annotation)).orElseGet(() ->
                findFirst(aClass.getInterfaces(), i -> getInterfaceAnnotations(i, annotation)).orElse(null));
    }

    // breadth first
    private <T extends Annotation> T getInterfaceAnnotations(Class<?>[] interfaces, Class<T> annotation) {
        return findFirst(interfaces, i -> i.getAnnotation(annotation)).orElseGet(() ->
                findFirst(interfaces, i -> getInterfaceAnnotations(i.getInterfaces(), annotation)).orElse(null));
    }

    private <T> T depthFirst2(Collection<T> roots, Traverser<T> traverser, UnaryOperator<T> visitor) {
        return findFirst(roots, r -> depthFirst2(r, traverser, visitor)).orElse(null);
    }

    private <T> T depthFirst2(T root, Traverser<T> traverser, UnaryOperator<T> visitor) {
        return ofNullable(visitor.apply(root)).orElseGet(() ->
                    depthFirst2(traverser.apply(root), traverser, visitor));
    }

    private <T> T breadthFirst2(Collection<T> roots, Traverser<T> traverser, UnaryOperator<T> visitor) {
        return findFirst(roots, visitor).orElseGet(() ->
                findFirst(roots, r -> breadthFirst2(traverser.apply(r), traverser, visitor)).orElse(null));
    }

    private <T,V> Optional<V> findFirst(T[] array, Function<T,V> function) {
        return stream(array).map(function).filter(Objects::nonNull).findFirst();
    }

    private <T,V> Optional<V> findFirst(Collection<T> coll, Function<T,V> function) {
        return coll.stream().map(function).filter(Objects::nonNull).findFirst();
    }

    interface Traverser<T> extends Function<T, Collection<T>> {}

    private <T> T depthFirst(Collection<T> list, Traverser<T> traverser, Predicate<T> visitor) {
        return list.stream()
                .map(l -> ofNullable(l).filter(visitor).orElseGet(() -> depthFirst(traverser.apply(l), traverser, visitor)))
                .filter(Objects::nonNull).filter(visitor).findFirst().orElse(null);
    }

    private <T> T breadthFirst(Collection<T> list, Traverser<T> traverser, Predicate<T> visitor) {
        return list.stream()
                .filter(visitor)
                .findFirst().orElseGet(() -> list.stream()
                                                 .map(l -> breadthFirst(traverser.apply(l), traverser, visitor))
                                                 .filter(Objects::nonNull).filter(visitor).findFirst().orElse(null));
    }
}
