
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

public final class moran103 extends Circuit { 
    public static String CIRCUIT_DIGEST = "e8d1c11b3b613aff47db1870face047f";

    public void doCompute(String roomId,String[] args, List<String> takerList, List<String> resultReceiverList) throws MPCException {
        super.startTask(roomId, CIRCUIT_DIGEST, args, takerList, resultReceiverList);
    }

    public void setInputCallbackForORG(InputCallback<Int64> callback) {
        int[] dimesionTpl = {13};
        callback.setDimesions(dimesionTpl);
        super.addInputCallback(callback,CIRCUIT_DIGEST, MPCTaskType.ALICE);
    }

    public void setInputCallbackForDST(InputCallback<Int64[]> callback) {
        int[] dimesionTpl = {13};
        callback.setDimesions(dimesionTpl);
        super.addInputCallback(callback, CIRCUIT_DIGEST,MPCTaskType.BOB);
    }

    public void setOutputCallbackForORG(OutputCallback<Int64> callback) {
        int[] dimesionTpl = {13};
        callback.setDimesions(dimesionTpl);
        super.addOutputCallback(callback, CIRCUIT_DIGEST,MPCTaskType.ALICE);
    }


    public void setOutputCallbackForDST(OutputCallback<Int64> callback) {
        int[] dimesionTpl = {13};
        callback.setDimesions(dimesionTpl);
        super.addOutputCallback(callback, CIRCUIT_DIGEST, MPCTaskType.BOB);
    }

    public moran103(String algId, String user, String password, NodeCommunicateMode mode, String proxyEndpoint, String jugoEndpoint, String[] args) {
        super(user, password, mode, proxyEndpoint, jugoEndpoint, args);
        CIRCUIT_DIGEST = algId;
    }
}
