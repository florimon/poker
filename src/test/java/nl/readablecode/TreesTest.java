package nl.readablecode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static nl.readablecode.TreesTest.MyClass.el;

public class TreesTest {

    @ToString(of = "i")
    @Getter
    @RequiredArgsConstructor
    static class MyClass {
        final int i;
        final List<MyClass> children;

        static MyClass el(int i, MyClass... children) {
            return new MyClass(i, new ArrayList<>(Arrays.asList(children)));
        }
    }

    @Test
    public void testIt() {
        List<MyClass> list = new ArrayList<>(asList(el(1, el(11)), el(2, el(22)), el(3, el(33))));
        System.out.println(flattenBreadthFirst(list, MyClass::getChildren));
        System.out.println(flattenDepthFirst(list, MyClass::getChildren));

        List<Integer> integers = new ArrayList<>();
        breadthFirst(list, MyClass::getChildren, myclass -> !integers.add(myclass.i));
        System.out.println(integers);

        MyClass myClass1 = breadthFirst(list, MyClass::getChildren, myClass -> myClass.i == 22);
        System.out.println(myClass1);
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

    private <T,V> Optional<V> findFirst(T[] array, Function<T,V> function) {
        return stream(array).map(function).filter(Objects::nonNull).findFirst();
    }

    interface Traverser<T> extends Function<T, Collection<T>> {}

    private <T> List<T> flattenBreadthFirst(Collection<T> list, Traverser<T> traverser) {
        return combine(list,
                list.stream().flatMap(l -> flattenBreadthFirst(traverser.apply(l), traverser).stream()).collect(toList()));
    }

    private <T> List<T> flattenDepthFirst(Collection<T> list, Traverser<T> traverser) {
        return list.stream().flatMap(l -> prepend(l, flattenDepthFirst(traverser.apply(l), traverser)).stream()).collect(toList());
    }

    private <T> List<T> prepend(T head, Collection<T> collection) {
        ArrayList<T> result = new ArrayList<>();
        result.add(head);
        result.addAll(collection);
        return result;
    }

    private <T> List<T> combine(Collection<T> collection1, Collection<T> collection2) {
        ArrayList<T> result = new ArrayList<>();
        result.addAll(collection1);
        result.addAll(collection2);
        return result;
    }

//    private <T> T depthFirst(List<T> list, Traverser<T> traverser, Predicate<T> visitor) {
//        list.stream().filter(visitor).findFirst()
//    }

    private <T> T breadthFirst(Collection<T> list, Traverser<T> traverser, Predicate<T> visitor) {
        return list.stream().filter(visitor).findFirst().orElseGet(() ->
            list.stream().map(l -> breadthFirst(traverser.apply(l), traverser, visitor)).filter(Objects::nonNull).filter(visitor).findFirst()
                .orElse(null));
    }
}
