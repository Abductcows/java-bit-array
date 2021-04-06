package gr.geompokon.bitarray;

class ImplBitUtilities {
    long getBitMask(int bitIndex) {
        return 1L << (63 - bitIndex);
    }

    long getSelectionMask(int index) {
        return index == 0 ?
                -1 :
                getBitMask(index - 1) - 1;
    }

    long getBitsStartToIndexExclusive(int indexExclusive, long theLong) {
        return theLong & (~getSelectionMask(indexExclusive));
    }

    long getBitsIndexToEnd(int index, long theLong) {
        return theLong & getSelectionMask(index);
    }
}