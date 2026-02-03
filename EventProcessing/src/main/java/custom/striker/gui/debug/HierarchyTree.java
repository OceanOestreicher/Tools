package custom.striker.gui.debug;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import custom.striker.gui.Frame;

import javax.swing.*;
import java.util.List;

/**
 * Data structure used for storing the hierarchy of various AWT / Swing components
 */
public class HierarchyTree {

    private final Map<Component, TreeNode> componentHierarchyMap = new HashMap<>();

    private final TreeNode rootNode;

    private final Frame container;

    public HierarchyTree(Frame frame) {
        rootNode = new TreeNode(null, frame.getContentPanel());
        componentHierarchyMap.put(frame.getContentPanel(), rootNode);
        this.container = frame;
    }

    /**
     * Return the list of components whose bounds encompass the point
     * @param point The point to check
     * @return List of components which contain the point
     */
    public List<Component> getComponentsAtPoint(Point point) {
        return getComponentsAtPoint(rootNode, point);
    }

    private List<Component> getComponentsAtPoint(TreeNode currentNode, Point point) {
        List<Component> components = new ArrayList<>();

        if (!containsPoint(convertComponentRelativePointsToFrameRelativePoints(currentNode.component), point) || !currentNode.component.isVisible()) {
            return new ArrayList<>();
        }

        components.add(currentNode.component);

        for (TreeNode child: currentNode.children) {
            components.addAll(getComponentsAtPoint(child, point));
        }

        return components;
    }

    private boolean containsPoint(List<Point> componentPoints, Point point) {
        Point startPoint = componentPoints.get(0);
        Point endPoint = componentPoints.get(1);

        return point.x >= startPoint.x && point.x <= endPoint.x && point.y >= startPoint.y && point.y <= endPoint.y;
    }

    /**
     * Translates the component's starting and ending points to the correct points of its parent {@link Frame}
     * @param component The component to translate points from
     * @return List containing the translated points
     */
    private List<Point> convertComponentRelativePointsToFrameRelativePoints(Component component) {
        Point componentStartPoint = new Point(0, 0);
        Point componentEndPoint = new Point(component.getWidth(), component.getHeight());

        Point relativeStart = SwingUtilities.convertPoint(component, componentStartPoint, container);
        Point relativeEnd = SwingUtilities.convertPoint(component, componentEndPoint, container);

        return List.of(relativeStart, relativeEnd);
    }

    /**
     * Whether this tree contains the component somewhere inside itself
     * @param component The component to check
     * @return True if this tree has the component
     */
    public boolean containsComponent(Component component) {
        return componentHierarchyMap.containsKey(component);
    }

    /**
     * Insert the child component into the tree underneath its parent.
     * @param parent The parent of the child to insert
     * @param child The child to insert
     */
    public void insertComponent(Component parent, Component child) {
        TreeNode node = componentHierarchyMap.get(parent);

        TreeNode childNode = new TreeNode(node, child);
        node.children.add(childNode);
        componentHierarchyMap.put(child, childNode);
    }

    /**
     * Removes the component and its children from the tree
     * @param component Component to remove
     */
    public void removeComponent(Component component) {
        TreeNode componentNode = componentHierarchyMap.get(component);

        if (componentNode == null) {
            return;
        }

        for (TreeNode node : componentNode.children) {
            removeComponent(node.component);
        }

        if (componentNode.parent != null) {
            componentNode.parent.children.remove(componentNode);
        }

        componentHierarchyMap.remove(component);
    }

    private static class TreeNode {
        Component component;
        TreeNode parent;
        List<TreeNode> children = new ArrayList<>();

        public TreeNode(TreeNode parent, Component component) {
            this.component = component;
        }
    }
}
