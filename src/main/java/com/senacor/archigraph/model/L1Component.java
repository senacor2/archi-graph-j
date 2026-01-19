package com.senacor.archigraph.model;

import lombok.Getter;
import lombok.Setter;

public class L1Component extends Component {

    @Getter
    private final AppMatrix l1AppMatrix;
    @Getter
    private final int proxyAreaSize;

    public L1Component(String name, int row, int column, int width, int height, int proxyAreaSize) {
        super(name, row, column, width, height, 1);
        this.proxyAreaSize = proxyAreaSize;
        this.l1AppMatrix = new AppMatrix(height + 2*proxyAreaSize, width + 2*proxyAreaSize);
    }

}
