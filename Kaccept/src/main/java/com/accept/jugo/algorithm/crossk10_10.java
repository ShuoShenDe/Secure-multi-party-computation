
package com.accept.jugo.algorithm;

import com.google.common.primitives.Bytes;
import com.juzix.jugo.circuit.*;
import com.juzix.jugo.circuit.datatypes.*;
import com.juzix.jugo.node.common.MPCTaskType;
import com.juzix.jugo.node.exception.MPCException;
import com.juzix.jugo.slice.common.NodeCommunicateMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class crossk10_10 extends Circuit {
    public static String CIRCUIT_DIGEST = "6f6756974e46e3f720cf1bae796fc3c4";

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Structure
    public static class Point {
        private int __srcPos = 0;

        private Uint64 x;
        private Uint64 y;
        public byte[] toByte() {
            List<Byte> result = new ArrayList<>();

            result.addAll(CircuitEndoder.byteToByte(this.x.toByteArray(false)));
            result.addAll(CircuitEndoder.byteToByte(this.y.toByteArray(false)));

            byte[] finalResult = Bytes.toArray(result);
            return finalResult;
        }
        public crossk10_10.Point fromByte(byte[] input) {
            int srcPos = 0;
            int dstLen = 0;
            byte[] tempByteArray = null;

            dstLen = BYTE_SIZE_64_BIT;
            this.x = new Uint64(byteToBigInteger(input, srcPos, dstLen, false));
            srcPos += dstLen;

            dstLen = BYTE_SIZE_64_BIT;
            this.y = new Uint64(byteToBigInteger(input, srcPos, dstLen, false));
            srcPos += dstLen;

            this.__srcPos = srcPos;
            return this;
        }

        public void setX(Uint64 x) {
            this.x = x;
        }

        public void setY(Uint64 y) {
            this.y = y;
        }

        public Uint64 getX() {
            return x;
        }

        public Uint64 getY() {
            return y;
        }
    }


    public void doCompute(String roomId,String[] args, List<String> takerList, List<String> resultReceiverList) throws MPCException {
        super.startTask(roomId, CIRCUIT_DIGEST, args, takerList, resultReceiverList);
    }

    public void setInputCallbackForORG(InputCallback<crossk10_10.Point[]> callback) {
        int[] dimesionTpl = {10};
        callback.setDimesions(dimesionTpl);
        super.addInputCallback(callback,CIRCUIT_DIGEST, MPCTaskType.ALICE);
    }

    public void setInputCallbackForDST(InputCallback<crossk10_10.Point[]> callback) {
        int[] dimesionTpl = {10};
        callback.setDimesions(dimesionTpl);
        super.addInputCallback(callback, CIRCUIT_DIGEST, MPCTaskType.BOB);
    }

    public void setOutputCallbackForORG(OutputCallback<Uint64[]> callback) {
        int[] dimesionTpl = {100};
        callback.setDimesions(dimesionTpl);
        super.addOutputCallback(callback, CIRCUIT_DIGEST, MPCTaskType.ALICE);
    }


    public void setOutputCallbackForDST(OutputCallback<Uint64[]> callback) {
        int[] dimesionTpl = {100};
        callback.setDimesions(dimesionTpl);
        super.addOutputCallback(callback, CIRCUIT_DIGEST,  MPCTaskType.BOB);
    }

    public crossk10_10(String algId, String user, String password, NodeCommunicateMode mode, String proxyEndpoint, String jugoEndpoint, String[] args) {
        super(user, password, mode, proxyEndpoint, jugoEndpoint, args);
        CIRCUIT_DIGEST = algId;
    }
}
