import Pyro5.server
import time

import Pyro5.server
import threading

from dji.v5.manager.aircraft.virtualstick import VirtualStickManager
from dji.v5.manager.aircraft.virtualstick import VirtualStickState
from dji.v5.manager.aircraft.virtualstick import VirtualStickStateListener
from dji.v5.common.error import IDJIError;
from dji.v5.common.callback import CommonCallbacks
from  dji.sdk.keyvalue.value.flightcontroller import VirtualStickFlightControlParam
from dji.sampleV5.aircraft.models import DroneMover



@Pyro5.server.expose
class Process1:
    def foo1(self, arg):
        print(f"foo1 called with: {arg}")
        return f"foo1 received: {arg}"

    def enableVirtualStick(self, timeout:int=2):
        #return DroneMover.getInstance().enableVirtualStick(timeout)
        s=f'{DroneMover.getInstance()}'
        s+= 'QQ:' + DroneMover.getInstance().enableVirtualStick(timeout)
        return "back:" + s


def start_pyro_daemon():
    daemon = Pyro5.server.Daemon(host="0.0.0.0", port=9090)  # Create a Pyro daemon
    uri = daemon.register(Process1, objectId="process1")
    print(f"Ready. Object uri = {uri}")  # Print the object uri so the client can use it
    daemon.requestLoop()  # Start the event loop of the server to wait for calls


def mypython():
    # Run the daemon in a background thread
    daemon_thread = threading.Thread(target=start_pyro_daemon, daemon=True)
    daemon_thread.start()
    VirtualStickManager.getInstance()


    VirtualStickManager.getInstance().enableVirtualStick(None)
    virtualStickFlightControlParam=VirtualStickFlightControlParam()
    VirtualStickManager.getInstance().sendVirtualStickAdvancedParam(virtualStickFlightControlParam)
    print("HEJ")

    return 12
