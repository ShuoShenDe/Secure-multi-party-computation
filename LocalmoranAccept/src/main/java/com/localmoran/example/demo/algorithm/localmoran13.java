
package com.localmoran.example.demo.algorithm;

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

public final class localmoran13 extends Circuit {
    public static String CIRCUIT_DIGEST = "b160824a2d2344a47f87df20375928d9";

    public void doCompute(String roomId,String[] args, List<String> takerList, List<String> resultReceiverList) throws MPCException {
        super.startTask(roomId, CIRCUIT_DIGEST, args, takerList, resultReceiverList);
    }

    public void setInputCallbackForORG(InputCallback<Int32[]> callback) {
        int[] dimesionTpl = {13};
        callback.setDimesions(dimesionTpl);
        super.addInputCallback(callback,CIRCUIT_DIGEST, MPCTaskType.ALICE);
    }

    public void setInputCallbackForDST(InputCallback<Int32[]> callback) {
        int[] dimesionTpl = {13};
        callback.setDimesions(dimesionTpl);
        super.addInputCallback(callback, CIRCUIT_DIGEST,MPCTaskType.BOB);
    }

    public void setOutputCallbackForORG(OutputCallback<Int32[]> callback) {
        int[] dimesionTpl = {13};
        callback.setDimesions(dimesionTpl);
        super.addOutputCallback(callback, CIRCUIT_DIGEST,MPCTaskType.ALICE);
    }


    public void setOutputCallbackForDST(OutputCallback<Int32[]> callback) {
        int[] dimesionTpl = {13};
        callback.setDimesions(dimesionTpl);
        super.addOutputCallback(callback, CIRCUIT_DIGEST, MPCTaskType.BOB);
    }

    public localmoran13(String algId, String user, String password, NodeCommunicateMode mode, String proxyEndpoint, String jugoEndpoint, String[] args) {
        super(user, password, mode, proxyEndpoint, jugoEndpoint, args);
        CIRCUIT_DIGEST = algId;
    }
}
