#!/usr/bin/python

import Skype4Py
import sys
import time

# This variable will get its actual value in OnCall handler
CallStatus = 0

# Here we define a set of call statuses that indicate a call has been either aborted or finished
CallIsFinished = set ([Skype4Py.clsFailed, Skype4Py.clsFinished, Skype4Py.clsMissed,
    Skype4Py.clsRefused, Skype4Py.clsBusy, Skype4Py.clsCancelled]);

def AttachmentStatusText(status):
    return skype.Convert.AttachmentStatusToText(status)

def CallStatusText(status):
    return skype.Convert.CallStatusToText(status)

# This handler is fired when status of Call object has changed
def OnCall(call, status):
    global CallStatus
    global WavFile
    global OutFile
    CallStatus = status
    print 'Call status: ' + CallStatusText(status)
	if Status == "RINGING":
        print 'Incomming call...'
        print time.strftime("%a, %d %b %Y %H:%M:%S") + "PartnerDisplayName: " +Call.PartnerDisplayName + " PartnerHandle: "+Call.PartnerHandle +"  PstnNumber: " + Call.PstnNumber
        Call.Answer()
                     time.sleep(4)
                     print 'Opening door ...'
                     #Call.DTMF = "9"
                     print "SET CALL "+str(Call.Id)+" DTMF 9"
                     skype.SendCommand(skype.Command("SET CALL
"+str(Call.Id)+" DTMF 9"))
                     time.sleep(3)
                     Call.Finish()

	
	

HasConnected = False
voteState = 'WaitingForPIN'
PIN=''
rootDir='D:\\PunchScan2.0\\PunchScan2.0\\src\\SkypePython\\'
vote=''
finishedPlaying = True

def OnInputStatusChanged(call, status):
    global HasConnected
    global myCall
    global finishedPlaying

    myCall=call
    print 'InputStatusChanged: ',call.InputDevice(),call,status
    print ' inputdevice: ',call.InputDevice()
    # Hang up if finished
    if status == True:
        HasConnected = True
    if status == False and HasConnected == True:
        print ' play finished'
        finishedPlaying = True

def DtmfReceived(call, code):
    global PIN
    global voteState
    global vote

    #TODO stop everything you're playing

    print 'key pressed *' + code + '*'
    print 'state '+voteState

    if voteState == 'WaitingForPIN':
        PIN +=code

    if len(PIN) >=4:
        voteState='PlayingBallot'
        playBallot(call)
        PIN=''
        return

    if voteState == 'PlayingBallot':
        if  code =='1' or code == '2':
            vote=code
            voteState='CastOrSpoil'
            playYouVotedFor(call)
            playConfirmationCode(call)
            playToCastThisBallot(call)
        else:
            playInvalidInput(call)
            playBallot(call)
        return
    
    if voteState == 'CastOrSpoil':
        if code == '4':
            playGoodBye(call)
        elif code == '9':
                voteState = 'WaitingForPIN'
                playTypeYourPIN(call)
        else:
            playInvalidInput(call)
            playYouVotedFor(call)
            playConfirmationCode(call)
            playToCastThisBallot(call)
            

def playTypeYourPIN(call):
    global PIN
    global rootDir
    global finishedPlaying
    
    PIN=''
    print 'Playing PleaseTypeInYour4DigitPIN...'

    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False

    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'PleaseTypeInYour4DigitPIN.wav')

def playBallot(call):
    global rootDir
    global finishedPlaying
    print 'playing ballot...'

    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'ToVoteFor.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Alice.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Press.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'1.wav')


    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'ToVoteFor.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Bob.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Press.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'2.wav')
    
def playInvalidInput(call):
    global rootDir
    global finishedPlaying
    print 'playing InvalidInput...'

    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False    
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'InvalidInput.wav')


def playYouVotedFor(call):
    global rootDir
    global finishedPlaying
    global vote
    print 'playing YouVotedFor...'
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'YouVotedFor.wav')


    if vote == '1':
        while not finishedPlaying:
            time.sleep(0.1)
        finishedPlaying = False
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Alice.wav')
    elif vote == '2':
        while not finishedPlaying:
            time.sleep(0.1)
        finishedPlaying = False    
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Bob.wav')

def playConfirmationCode(call):
    global rootDir
    global finishedPlaying
    global vote
    print 'playing YourConfirmationCodeIs...'
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'YourConfirmationCodeIs.wav')

    if vote == '1':
        while not finishedPlaying:
            time.sleep(0.1)
        finishedPlaying = False
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'4.wav')

        while not finishedPlaying:
            time.sleep(0.1)
        finishedPlaying = False
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'D.wav')

        while not finishedPlaying:
            time.sleep(0.1)
        finishedPlaying = False
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'F.wav')        
    elif vote == '2':
        while not finishedPlaying:
            time.sleep(0.1)
        finishedPlaying = False
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'C.wav')

        while not finishedPlaying:
            time.sleep(0.1)
        finishedPlaying = False
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'A.wav')

        while not finishedPlaying:
            time.sleep(0.1)
        finishedPlaying = False
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'0.wav')

    
    
def playToCastThisBallot(call):
    global rootDir
    global finishedPlaying
    print 'playing ToCastThisBallot...'

    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'ToCastThisBallot.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Press.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'4.wav')

    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'ToSpoilThisBallot.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Press.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'9.wav')


def playGoodBye(call):
    global rootDir
    global finishedPlaying
    print 'playing GoodBye...'

    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'ThankYou.wav')
    
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'GoodBye.wav')
    while not finishedPlaying:
        time.sleep(0.1)
    finishedPlaying = False
	
    call.Finish()
    


# This handler is fired when status of Call object has changed
def OnCall(call, status):
    global CallStatus
    CallStatus = status
    print 'Call status: ' + CallStatusText(status)

    if status == Skype4Py.clsInProgress:
        playTypeYourPIN(call)

# This handler is fired when Skype attatchment status changes
def OnAttach(status):
    print 'API attachment status: ' + AttachmentStatusText(status)
    if status == Skype4Py.apiAttachAvailable:
        skype.Attach()

# Let's see if we were started with a command line parameter..
try:
    CmdLine = 'mypostefan'#sys.argv[1]
except:
    print 'Usage: python skypecall.py destination [wavtosend] [wavtorecord]'
    sys.exit()

# Creating Skype object and assigning event handlers..
skype = Skype4Py.Skype()

# Attatching to Skype..
print 'Connecting to Skype..'
skype.Attach()

skype.OnAttachmentStatus = OnAttach
skype.OnCallStatus = OnCall
skype.OnCallInputStatusChanged = OnInputStatusChanged
skype.OnCallDtmfReceived=DtmfReceived

# Starting Skype if it's not running already..
if not skype.Client.IsRunning:
    print 'Starting Skype..'
    skype.Client.Start()

# Attatching to Skype..
print 'Connecting to Skype..'
skype.Attach()

# Checking if what we got from command line parameter is present in our contact list
Found = False
for F in skype.Friends:
    if F.Handle == CmdLine:
        Found = True
        print 'Calling ' + F.Handle + '..'
        skype.PlaceCall(CmdLine)
        break
if not Found:
    print 'Call target not found in contact list'
    print 'Calling ' + CmdLine + ' directly.'
    skype.PlaceCall(CmdLine)

# Loop until CallStatus gets one of "call terminated" values in OnCall handler
while not CallStatus in CallIsFinished:
    time.sleep(0.1)
