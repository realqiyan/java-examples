package org.qiyan.examples.graph;

import com.alibaba.fastjson2.JSON;

import java.util.List;

public class NodeResult {
    private final String threadName;
    private final String nodeName;
    private final Object value;
    private final List<NodeResult> depends;

    public NodeResult(String nodeName, Object value, List<NodeResult> depends) {
        this.nodeName = nodeName;
        this.value = value;
        this.depends = depends;
        this.threadName = Thread.currentThread().getName();
    }

    public String getThreadName() {
        return threadName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public Object getValue() {
        return value;
    }

    public List<NodeResult> getDepends() {
        return depends;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
