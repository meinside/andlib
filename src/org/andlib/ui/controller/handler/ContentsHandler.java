/*
 Copyright (c) 2010, Sungjin Han <meinside@gmail.com>
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of meinside nor the names of its contributors may be
    used to endorse or promote products derived from this software without
    specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */

package org.andlib.ui.controller.handler;


import java.util.HashMap;

import org.andlib.helper.LogHelper;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * Manages ContentsHandlerWorkers for UI controllers. 
 * 
 * @author meinside@gmail.com
 * @since 09.11.20.
 * 
 * last update 10.02.23.
 *
 */
final public class ContentsHandler extends Handler
{
	public static final String KEY_WORKER_ID = "ContentsHandler.worker_id";
	
	public HashMap<Integer, ContentsHandlerWorker> workerMap;
	
	public ContentsHandler()
	{
		super();
		workerMap = new HashMap<Integer, ContentsHandlerWorker>();
	}
	
	/**
	 * send message to a specific worker
	 * @param msg null if none
	 * @param workerId
	 */
	public void sendMessageToWorker(Message msg, int workerId)
	{
		if(msg == null)
		{
			msg = Message.obtain();
		}
		msg.getData().putInt(KEY_WORKER_ID, workerId);
		sendMessage(msg);
	}

	/* (non-Javadoc)
     * @see android.os.Handler#handleMessage(android.os.Message)
     */
    @Override
    public void handleMessage(Message msg)
    {
    	int workerId = msg.getData().getInt(KEY_WORKER_ID); 

    	Log.v(LogHelper.where(), "handling message for worker with id:" + workerId);

    	ContentsHandlerWorker worker = workerMap.get(workerId);
    	if(worker != null)
    		worker.doSomething(msg);
    	else
    		Log.d(LogHelper.where(), "no such worker with id: " + workerId);
    }
	
    /**
     * add a new worker to this handler
     * @param id
     * @param worker
     */
    synchronized public void addWorker(ContentsHandlerWorker worker)
    {
    	if(worker != null)
    	{
    		Log.v(LogHelper.where(), "adding a worker with id: " + worker.getId());
    		
    		workerMap.put(worker.getId(), worker);
    	}
    }
    
    /**
     * remove a worker from this handler
     * @param id
     */
    synchronized public void removeWorker(int id)
    {
    	if(workerMap.remove(id) == null)
    		Log.d(LogHelper.where(), "no such worker with id: " + id);
    	else
    		Log.v(LogHelper.where(), "removing a worker with id: " + id);
    }
}
