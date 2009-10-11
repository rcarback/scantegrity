#!/usr/bin/python

import Skype4Py
import sys
import time

# Here we define a set of call statuses that indicate a call has been either aborted or finished
CallIsFinished = set ([Skype4Py.clsFailed, Skype4Py.clsFinished, Skype4Py.clsMissed,
    Skype4Py.clsRefused, Skype4Py.clsBusy, Skype4Py.clsCancelled]);

def AttachmentStatusText(status):
    return skype.Convert.AttachmentStatusToText(status)

def CallStatusText(status):
    return skype.Convert.CallStatusToText(status)

# This handler is fired when status of Call object has changed
def AnswerIncommingCall(call, status):
    print 'Call status: ' + CallStatusText(status)
    if status == "RINGING":
        print 'Incomming call...'
        print time.strftime("%a, %d %b %Y %H:%M:%S") + "PartnerDisplayName: " +call.PartnerDisplayName + " PartnerHandle: "+call.PartnerHandle +"  PstnNumber: " + call.PstnNumber
        call.Answer()
        time.sleep(1)
            
# This handler is fired when status of Call object has changed
def OnCall(call, status):
    print 'Call status: ' + CallStatusText(status)

    if status == Skype4Py.clsInProgress:
        playTypeYourPIN(call)

            
            
HasConnected = False
voteState = 'WaitingForPIN'
PIN=''
rootDir='D:\\PunchScan2.0\\PunchScan2.0\\src\\SkypePython\\'
vote=''
finishedPlaying = True

waitingForInput = True



def OnInputStatusChanged(call, status):
    global HasConnected
    global myCall
    global finishedPlaying

    myCall=call
    #print 'InputStatusChanged: ',call.InputDevice(),call,status
    #print ' inputdevice: ',call.InputDevice()
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
    global waitingForInput

    
    print 'key pressed *' + code + '*'
    print 'state '+voteState
    print 'waitingForInput ',waitingForInput

    
    if not waitingForInput:
        return
    else:
        waitingForInput	= False

    stateMachine(call, code)

def stateMachine(call, code):
    global PIN
    global voteState
    global vote
    global waitingForInput
    
    if voteState == 'WaitingForPIN':
        PIN +=code
        waitingForInput	= True
    
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
    global voteState
    global waitingForInput

    
    PIN=''
    print 'Playing PleaseTypeInYour4DigitPIN...'

    waitingForInput = False
    waitForWavToFinish()	
    voteState = 'WaitingForPIN'
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'PleaseTypeInYour4DigitPIN.wav')
    waitingForInput = True

def playBallot(call):
    global rootDir
    global finishedPlaying
    global waitingForInput

    
    print 'playing ballot...'

    waitingForInput = True
    
    waitForWavToFinish()
    if waitingForInput:
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'ToVoteFor.wav')
    
    waitForWavToFinish()
    if waitingForInput:
            call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Alice.wav')
    
    waitForWavToFinish()
    if waitingForInput:
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Press.wav')
    
    waitForWavToFinish()
    if waitingForInput:
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'1.wav')


    
    waitForWavToFinish()
    if waitingForInput:
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'ToVoteFor.wav')
    
    waitForWavToFinish()
    if waitingForInput:
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Bob.wav')
    
    waitForWavToFinish()
    if waitingForInput:
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Press.wav')
    
    waitForWavToFinish()
    if waitingForInput:
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'2.wav')
    
def playInvalidInput(call):
    global rootDir
    global finishedPlaying
    global waitingForInput

    print 'playing InvalidInput...'
    
    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'InvalidInput.wav')

def playYouVotedFor(call):
    global rootDir
    global finishedPlaying
    global vote
    print 'playing YouVotedFor...'
    
    #waitingForInput = False
    
    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'YouVotedFor.wav')

    if vote == '1':
        waitForWavToFinish()
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Alice.wav')
    elif vote == '2':
        waitForWavToFinish()
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Bob.wav')
    
    #waitingForInput = True

def playConfirmationCode(call):
    global rootDir
    global finishedPlaying
    global vote
    global waitingForInput
    
    
    print 'playing YourConfirmationCodeIs...'
    
    #waitingForInput = False
    
    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'YourConfirmationCodeIs.wav')

    if vote == '1':
        waitForWavToFinish()
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'4.wav')

        waitForWavToFinish()
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'D.wav')

        waitForWavToFinish()
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'F.wav')        
    elif vote == '2':
        waitForWavToFinish()
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'C.wav')

        waitForWavToFinish()
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'A.wav')

        waitForWavToFinish()
        call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'0.wav')

    #waitingForInput = True
    
def playToCastThisBallot(call):
    global rootDir
    global finishedPlaying
    global waitingForInput
    
    
    print 'playing ToCastThisBallot...'

    #waitingForInput = False
    
    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'ToCastThisBallot.wav')
    
    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Press.wav')
    
    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'4.wav')

    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'ToSpoilThisBallot.wav')
    
    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'Press.wav')
    
    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'9.wav')

    waitingForInput = True

def waitForWavToFinish():
    global finishedPlaying
    global waitingForInput
    
    time.sleep(0.1)
    while not finishedPlaying:
        time.sleep(0.1)
        print 'waitingForInput ',waitingForInput
    finishedPlaying = False
	
def playGoodBye(call):
    global rootDir
    global finishedPlaying
    global waitingForInput
    
    print 'playing GoodBye...'

    waitingForInput = False
    
    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'ThankYou.wav')
    
    waitForWavToFinish()
    call.InputDevice( Skype4Py.callIoDeviceTypeFile ,rootDir+'GoodBye.wav')
    
    waitForWavToFinish()
    call.Finish()
    
    waitingForInput = True
    
# This handler is fired when Skype attatchment status changes
def OnAttach(status):
    print 'API attachment status: ' + AttachmentStatusText(status)
    if status == Skype4Py.apiAttachAvailable:
        skype.Attach()
    


# Creating Skype object and assigning event handlers..
skype = Skype4Py.Skype()

skype.OnAttachmentStatus = OnAttach
skype.OnCallStatus = OnCall
skype.OnCallInputStatusChanged = OnInputStatusChanged
skype.OnCallDtmfReceived=DtmfReceived

# Starting Skype if it's not running already..
if not skype.Client.IsRunning:
    print 'Starting Skype..'
    skype.Client.Start()
    
skype.UnregisterEventHandler('CallStatus', AnswerIncommingCall)
skype.RegisterEventHandler('CallStatus', AnswerIncommingCall) 

# Attatching to Skype..
print 'Connecting to Skype..'
skype.Attach()
print 'Your full name:', skype.CurrentUser.FullName
while 1==1:
    time.sleep(0.1)
