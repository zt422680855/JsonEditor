package com.jsoneditor.edits;

import com.intellij.ui.treeStructure.Tree;
import com.jsoneditor.node.TreeNode;
import com.jsoneditor.node.ArrayNode;

import javax.swing.tree.TreePath;
import javax.swing.undo.CannotUndoException;

/**
 * @Description:
 * 1、新增子节点时，父节点是对象或数组类型
 * 2、新增兄弟节点
 * @Author: zhengt
 * @CreateDate: 2020/8/11 22:14
 */
public class AddEdit extends TreeEdit {

    // 待添加的节点
    protected TreeNode target;

    // 添加到该父节点下
    private TreeNode parent;

    // 添加到父节点下的位置
    private int index;

    public AddEdit(Tree tree, TreeNode target, TreeNode parent, int index) {
        super(tree);
        this.target = target;
        this.parent = parent;
        this.index = index;
    }

    @Override
    public void doAction() {
        treeModel.insertNodeInto(target, parent, index);
        if (parent instanceof ArrayNode) {
            // 新增子节点时，如果父节点时Array类型，子节点key设置为索引位置
            target.key = String.valueOf(parent.getIndex(target));
        }
        parent.updateNode();
        tree.expandPath(new TreePath(parent.getPath()));
    }

    @Override
    public void undo() throws CannotUndoException {
        treeModel.removeNodeFromParent(target);
        parent.updateNode();
        super.undo();
    }

}
