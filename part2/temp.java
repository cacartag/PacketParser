            case "ip":
                boolean continueLoopIp = ((packetsToCapture == -1) ? true: ((packetsToCapture != 0) ? true: false));
                int counterIp = 1;
                
                // new variables created for ip fragment reassembly
                //AtomicInteger mainDone = new AtomicInteger(0);
                //Vector<String> fragmentIDs = new Vector<String>();
                //ConcurrentLinkedQueue<Map<String,IPPacketParser>> packetQueue = new ConcurrentLinkedQueue<Map<String,IPPacketParser>>();
                //ConcurrentLinkedQueue<FragmentModel> reassembledPacketQueue = new ConcurrentLinkedQueue<FragmentModel>();
                //Vector<IPFragmentAssembler> threadVector = new Vector<IPFragmentAssembler>();
                //FragmentAdministrator adminThread = new FragmentAdministrator(reassembledPacketQueue,mainDone);
                
                adminThread.start();
                
                while(continueLoopIp)
                {
            
                    eth = new EthernetParser();
                    ip = new IPPacketParser();
                    
                    byte []  packet = getPacket();

                    if(packet.length > 33)
                        eth.parsePacket(packet);
                    if(eth.getTypeString().equals("0800"))
                    {
                        ip.parsePacket(packet);
                        
                        // check that the address passes the ip address filter, if not set it will always return true
                        if(checkIPAddressFilter(ip.getSourceAddressString(),ip.getDestinationAddressString()))
                        {
                            if(ip.getIfFragment() == true)
                            {
                                //System.out.println("Detected Fragment");
                                if(!fragmentIDs.contains(ip.getIdentification()))
                                {
                                    
                                    // new id received
                                    
                                    IPFragmentAssembler ipf = new IPFragmentAssembler(packetQueue,ip.getIdentification(),reassembledPacketQueue);
                                    ipf.start();
                                    Map<String,IPPacketParser> toThread = new HashMap<String,IPPacketParser>();
                                    toThread.put(ip.getIdentification(),ip);
                                    packetQueue.add(toThread);
                                    threadVector.addElement(ipf);
                                    fragmentIDs.addElement(ip.getIdentification());
                                    
                                    System.out.println("thread with ID: "+ ip.getIdentification());
                                }else{
                                    
                                    // already received this packets ID
                                    
                                    Map<String,IPPacketParser> toThread = new HashMap<String,IPPacketParser>();
                                    toThread.put(ip.getIdentification(),ip);
                                    packetQueue.add(toThread);
                                }
                            } else {
                            
                                if(headerOnly)
                                {
                                    ip.printHeaderOnly();
                                } else {
                                    ip.printAll();
                                }
                                
                            }
                            
                            if(counterIp == packetsToCapture)
                            {
                                continueLoopIp = false;
                            }
                            
                            counterIp = counterIp + 1;    


                        }
                    }
                    
                    if(doneReading)
                    {
                        continueLoopIp = false;
                    }


                }
                
                threads = threadVector.toArray();
                //
                //boolean threadsStillAlive = true;
                
                while(threadsStillAlive)
                {
                    threadsStillAlive = false;
                    if(threads != null)
                    {
                        for(int x = 0; x < threads.length; x++)
                        {
                            if(((IPFragmentAssembler)threads[x]).isAlive())
                            {
                                threadsStillAlive = true;
                            }
                        }
                    }
                }
                mainDone.set(1);                
                