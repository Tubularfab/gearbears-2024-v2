// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Utils.InputsManager.SwerveInputsManager;
import frc.robot.commands.SwerveJoystickCmd;
import frc.robot.subsystems.SwerveSubsystem;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;





/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer
{
    // The robot's subsystems and commands are defined here...
    private final Shooter m_shooter = new Shooter();
    private final Intake m_intake = new Intake();

    
    // Replace with CommandPS4Controller or CommandJoystick if needed
    private final XboxController driverController =
            new XboxController(OperatorConstants.DRIVER_CONTROLLER_PORT);
    
    private final SwerveJoystickCmd joystickCmd;
    private final SwerveInputsManager swerveInputsManager;
    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer()
    {
        swerveInputsManager = new SwerveInputsManager(()-> -driverController.getLeftY(),
                                        ()-> -driverController.getLeftX(),
                                        ()-> -driverController.getRightX(),
                                        ()->true,
                                        0.4,
                                        0.75);
        joystickCmd = new SwerveJoystickCmd(swerveInputsManager);
        SwerveSubsystem.getInstance().setDefaultCommand(joystickCmd);
        //Configure the trigger bindings
        configureBindings();
    }
    
    
    /**
     * Use this method to define your trigger->command mappings. Triggers can be created via the
     * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
     * predicate, or via the named factories in {@link
     * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
     * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
     * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
     * joysticks}.
     */
    private void configureBindings()
    {
        // Schedule `ExampleCommand` when `exampleCondition` changes to `true`

        
        // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
        // cancelling on release.
        new Trigger(driverController::getBButtonPressed).onTrue(Commands.runOnce(SwerveSubsystem.getInstance()::zeroHeading, SwerveSubsystem.getInstance()));
        //new Trigger(driverController::get).onTrue(Commands.runOnce(SwerveSubsystem.getInstance()::zeroHeading, SwerveSubsystem.getInstance()));
        new Trigger(driverController::getAButtonPressed).whileTrue(new ParallelCommandGroup( 
            m_shooter.getRunReverseShooter(),
            m_intake.getRunOutakeCommand()
            )
         );
         //Run reverse intake when A button is pressed (temp, change to LB when possible)
        new Trigger(driverController::getXButtonPressed).whileTrue(new SequentialCommandGroup(
        m_shooter.getStartShooterCommand(),
        new WaitCommand(1.5),
        m_intake.getRunIntakeCommand()));

        //shoot and intake (to get enough oomph into speaker) while X button is pressed (temp, change to RB)
        new Trigger(driverController::getRightBumper).whileTrue(m_shooter.getShooterCommand()); //shoot when RB Button is pressed (temp, change to RT)
        new Trigger(driverController::getLeftBumper).whileTrue(m_intake.getRunIntakeCommand()); //intake when LB is pressed(temp, change to LT)
        new Trigger(driverController::getYButtonPressed).whileTrue(m_shooter.getSlowShootCommand()); // slow shoot when Y button pressed for speaker (not temp :)





        
        
    }
    
    
    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand()
    {
        // An example command will be run in autonomous
        return new SequentialCommandGroup(
            m_shooter.getStartShooterCommand(),
            new WaitCommand(.50), //start shooter to use intake, get motors fired up

            m_intake.getRunIntakeCommand().withTimeout(.5),
            m_shooter.getStopCommand(), //run the intake then stop intake and shooter after .5 seconds


        
        );




    }
}
