/*
 *  Copyright (C) 2010-2013 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.action.swf4;

import com.jpexs.decompiler.flash.action.Action;
import com.jpexs.decompiler.flash.action.treemodel.ConstantPool;
import com.jpexs.decompiler.flash.action.treemodel.DecrementTreeItem;
import com.jpexs.decompiler.flash.action.treemodel.GetVariableTreeItem;
import com.jpexs.decompiler.flash.action.treemodel.IncrementTreeItem;
import com.jpexs.decompiler.flash.action.treemodel.PostDecrementTreeItem;
import com.jpexs.decompiler.flash.action.treemodel.PostIncrementTreeItem;
import com.jpexs.decompiler.flash.action.treemodel.SetVariableTreeItem;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import com.jpexs.decompiler.flash.helpers.Highlighting;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class ActionSetVariable extends Action {

    public ActionSetVariable() {
        super(0x1D, 0);
    }

    @Override
    public String toString() {
        return "SetVariable";
    }

    @Override
    public void translate(Stack<GraphTargetItem> stack, List<GraphTargetItem> output, java.util.HashMap<Integer, String> regNames, HashMap<String, GraphTargetItem> variables, HashMap<String, GraphTargetItem> functions) {
        GraphTargetItem value = stack.pop();
        GraphTargetItem name = stack.pop();
        variables.put(Highlighting.stripHilights(name.toStringNoQuotes((ConstantPool) null)), value);
        if (value instanceof IncrementTreeItem) {
            GraphTargetItem obj = ((IncrementTreeItem) value).object;
            if (!stack.isEmpty()) {
                if (stack.peek().equals(obj)) {
                    stack.pop();
                    stack.push(new PostIncrementTreeItem(this, obj));
                    return;
                }
            }
        }
        if (value instanceof DecrementTreeItem) {
            GraphTargetItem obj = ((DecrementTreeItem) value).object;
            if (!stack.isEmpty()) {
                if (stack.peek().equals(obj)) {
                    stack.pop();
                    stack.push(new PostDecrementTreeItem(this, obj));
                    return;
                }
            }
        }
        if (value instanceof IncrementTreeItem) {
            if (((IncrementTreeItem) value).object instanceof GetVariableTreeItem) {
                if (((GetVariableTreeItem) ((IncrementTreeItem) value).object).name.equals(name)) {
                    output.add(new PostIncrementTreeItem(this, ((IncrementTreeItem) value).object));
                    return;
                }
            }
        }
        if (value instanceof DecrementTreeItem) {
            if (((DecrementTreeItem) value).object instanceof GetVariableTreeItem) {
                if (((GetVariableTreeItem) ((DecrementTreeItem) value).object).name.equals(name)) {
                    output.add(new PostDecrementTreeItem(this, ((DecrementTreeItem) value).object));
                    return;
                }
            }
        }
        SetVariableTreeItem svt = new SetVariableTreeItem(this, name, value);
        output.add(svt);
    }
}
