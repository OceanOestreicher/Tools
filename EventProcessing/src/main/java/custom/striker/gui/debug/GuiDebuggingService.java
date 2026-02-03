package custom.striker.gui.debug;

import custom.striker.gui.ContentPanel;
import custom.striker.gui.Frame;
import custom.striker.gui.TabbedPane;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Registry used when debugging JSwing applications. These applications must make use of the {@link Frame} and
 * {@link ContentPanel} components in order for debugging to work.
 */
public class GuiDebuggingService {

    private static final List<Frame> frames = new ArrayList<>();
    private static final Map<Frame, HierarchyTree> frameTreeMap = new HashMap<>();
    private static final Map<Component, List<Component>> orphanMap = new HashMap<>();

    /**
     * Registers a frame for debugging. A {@link HierarchyTree} will be built for this frame
     * which contains the hierarchal representation of all components added to this frame. The frame must
     * use {@link ContentPanel}, {@link TabbedPane}, and {@link ScrollPane} objects in order for the hierarchy to be constructed. If debugging has not been enabled,
     * this method does nothing.
     *
     * @param frame The frame to register for debugging
     */
    public static void registerFrame(Frame frame) {
        if (DebugInspector.DEBUG_ENABLED && !frames.contains(frame)) {
            frames.add(frame);
            frameTreeMap.put(frame, new HierarchyTree(frame));
            DebugInspector.installDebugInspector(frame);
        }
    }

    /**
     * Unregisters the frame and removes the associated {@link HierarchyTree} of its components. If debugging is not
     * enabled, this method does nothing.
     *
     * @param frame The frame to unregister
     */
    public static void unregisterFrame(Frame frame) {
        if (!DebugInspector.DEBUG_ENABLED) {
            return;
        }

        frames.remove(frame);
        frameTreeMap.remove(frame);
    }

    /**
     * Updates the associated {@link HierarchyTree} of the {@link Frame} that the parent and child belong to. This method
     * is used by {@link ContentPanel}. If debugging is not enabled, this method simply returns.
     *
     * @param parent The parent component that was added to
     * @param child The child component that was added.
     */
    public static void updateHierarchyTree(Component parent, Component child) {
        if (!DebugInspector.DEBUG_ENABLED) {
            return;
        }

        // If the parent is not an orphan and belongs to a Frame already, add the child to it
        // and add any orphaned components whose parent is the child component
        for (Frame frame: frames) {
            HierarchyTree tree = frameTreeMap.get(frame);
            if (tree.containsComponent(parent)) {
                tree.insertComponent(parent, child);

                insertOrphans(tree, child);

                return;
            }
        }

        // If no Frame has the parent in its tree, then it is a parent of orphan components. We will
        // store these until we find a parent / child component pair where the parent is in a Frame's HierarchyTree.
        if (!orphanMap.containsKey(parent)) {
            orphanMap.put(parent, new LinkedList<>());
        }

        orphanMap.get(parent).add(child);
    }

    /**
     * This adds all orphaned components and their children to the parent's node of the {@link HierarchyTree} that the
     * parent belongs to
     * @param tree The tree which contains the parent component
     * @param parent The parent to add the orphaned components to in the HierarchyTree
     */
    private static void insertOrphans(HierarchyTree tree, Component parent) {
        if (!orphanMap.containsKey(parent)) {
            return;
        }

        for (Component orphan: orphanMap.get(parent)) {
            tree.insertComponent(parent, orphan);
            insertOrphans(tree, orphan);
        }
        orphanMap.remove(parent);
    }

    /**
     * Gets the list of components whose bounds encompass the designated point. This will only be components that the
     * developer has added. If debugging is not enabled or if the frame is not enabled, this returns an empty list.
     *
     * @param frame The frame to get components from
     * @param point The point in that frame to find components who encompass it
     * @return A list of components whose bounds contain the point
     */
    public static List<Component> getComponentsAtPoint(Frame frame, Point point) {
        if (!DebugInspector.DEBUG_ENABLED || !frame.isEnabled()) {
            return new ArrayList<>();
        }

        return frameTreeMap.get(frame).getComponentsAtPoint(point);
    }

    /**
     * Removes the component and its children from the associated {@link HierarchyTree}
     * @param component Component to remove
     */
    public static void removeComponentFromTree(Component component) {
        if (!DebugInspector.DEBUG_ENABLED) {
            return;
        }

        for (Frame frame: frames) {
            HierarchyTree tree = frameTreeMap.get(frame);
            if (tree.containsComponent(component)) {
                tree.removeComponent(component);
                return;
            }
        }
    }
}
