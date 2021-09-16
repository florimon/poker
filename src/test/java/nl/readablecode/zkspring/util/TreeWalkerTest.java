package nl.readablecode.zkspring.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static nl.readablecode.zkspring.util.TreeWalkerTest.Node.node;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TreeWalkerTest {

    private List<Node> nodes = new ArrayList<>(asList(
                                    node(1, node(11, node(111), node(112)), node(12)),
                                    node(2, node(22, node(221), node(222))),
                                    node(3, node(33, node(331), node(332)))));
    @Test
    public void shouldCollectAllNodesInCorrectOrderWhenWalkingDepthFirstAndVisitorAlwaysReturnsFalse() {
        List<Integer> ids = new ArrayList<>();
        TreeWalker<Node> treeWalker = new TreeWalker<>(Node::getChildren, node -> !ids.add(node.id));
        Node candidate = treeWalker.depthFirst(nodes);
        assertNull(candidate);
        assertEquals(asList(1, 11, 111, 112, 12, 2, 22, 221, 222, 3, 33, 331, 332), ids);
    }

    @Test
    public void shouldCollectSomeNodesInCorrectOrderWhenWalkingDepthFirstAndVisitorReturnsTrueForSomeNode() {
        List<Integer> ids = new ArrayList<>();
        TreeWalker<Node> treeWalker = new TreeWalker<>(Node::getChildren, node -> ids.add(node.id) && node.id == 2);
        Node candidate = treeWalker.depthFirst(nodes);
        assertEquals(2, candidate.id);
        assertEquals(asList(1, 11, 111, 112, 12, 2), ids);
    }

    @Test
    public void shouldCollectAllNodesInCorrectOrderWhenWalkingBreadthFirstAndVisitorAlwaysReturnsFalse() {
        List<Integer> ids = new ArrayList<>();
        TreeWalker<Node> treeWalker = new TreeWalker<>(Node::getChildren, node -> !ids.add(node.id));
        Node candidate = treeWalker.breadthFirst(nodes);
        assertNull(candidate);
        assertEquals(asList(1, 2, 3, 11, 12, 111, 112, 22, 221, 222, 33, 331, 332), ids);
    }

    @Test
    public void shouldCollectSomeNodesInCorrectOrderWhenWalkingBreadthFirstAndVisitorReturnsTrueForSomeNode() {
        List<Integer> ids = new ArrayList<>();
        TreeWalker<Node> treeWalker = new TreeWalker<>(Node::getChildren, node -> ids.add(node.id) && node.id == 2);
        Node candidate = treeWalker.breadthFirst(nodes);
        assertEquals(2, candidate.id);
        assertEquals(asList(1, 2), ids);
    }

    @ToString(of = "id")
    @Getter
    @RequiredArgsConstructor
    static class Node {
        final int id;
        final List<Node> children;

        static Node node(int id, Node... children) {
            return new Node(id, new ArrayList<>(Arrays.asList(children)));
        }
    }
}
