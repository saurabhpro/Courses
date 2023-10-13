package week2.miniproject_2;

/**
 * A class to represent a virtual, in-memory filesystem. PCDPFilesystem exposes
 * two user-visible APIs: one for adding a file to a given virtual filesystem
 * (addFile) and one for reading the contents of a file that already exists
 * (readFile).
 */
public class PCDPFilesystem {
    /**
     * The root folder for the virtual filesystem.
     */
    private final PCDPFolder root = new PCDPFolder("static");

    /**
     * Default constructor, creating an empty filesystem.
     */
    public PCDPFilesystem() {
    }

    /**
     * Add a file at the provided path with the provided contents.
     *
     * @param path     PCDPPath to the new file to create
     * @param contents Contents of the new file
     */
    public void addFile(PCDPPath path, String contents) {
        assert path.getNComponents() > 0;

        assert path.getComponent(0).equals(root.getName());

        var componentIndex = 1;
        var curr = root;
        while (componentIndex < path.getNComponents()) {
            final var component = path.getComponent(componentIndex++);
            final var next = curr.getChild(component);

            if (componentIndex < path.getNComponents()) {
                // Haven't reached bottom of path yet so must be folder
                if (next == null) {
                    final var newFolder = new PCDPFolder(component);
                    curr.addChild(newFolder);
                    curr = newFolder;
                } else {
                    assert next instanceof PCDPFolder;
                    curr = (PCDPFolder) next;
                }
            } else {
                // Reached base filename
                assert next == null;
                final var newFile = new PCDPFile(component, contents);
                curr.addChild(newFile);
            }
        }
    }

    /**
     * Read the file specified by path.
     *
     * @param path The absolute path to the file to read.
     *
     * @return The contents of the specified file, or null if the file does not
     * seem to exist.
     */
    public String readFile(PCDPPath path) {
        if (path.getNComponents() == 0) {
            return null;
        }

        if (!path.getComponent(0).equals(root.getName())) {
            return null;
        }

        var componentIndex = 1;
        PCDPFilesystemComponent curr = root;
        while (componentIndex < path.getNComponents()) {
            final var nextComponent = path.getComponent(componentIndex++);

            if (curr == null || !(curr instanceof PCDPFolder)) {
                return null;
            }

            final var next = ((PCDPFolder) curr).getChild(
                    nextComponent);

            curr = next;
        }

        if (curr == null || !(curr instanceof PCDPFile)) {
            return null;
        } else {
            return ((PCDPFile) curr).read();
        }
    }
}
