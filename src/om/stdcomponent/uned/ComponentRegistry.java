package om.stdcomponent.uned;

import om.OmDeveloperException;
import om.stdquestion.QComponentManager;

// UNED: 21-06-2011 - dballestin
/**
 * Just a list of standard components.<br/><br/>
 * This version of ComponentRegistry class has been used to register some new components as standard and
 * to replace some existing components with versions with new properties/functionalities.<br/><br/> 
 * They are needed to be able to make a generic class for question.xml
 */
public class ComponentRegistry
{
	private static final Class<?>[] COMPONENTCLASSES=
	{
		// When adding extra standard components, put them here
		AdvancedFieldComponent.class,
		AppletComponent.class,
		AudioComponent.class,		
		BoxComponent.class,
		BreakComponent.class,
		ButtonComponent.class,
		CanvasComponent.class,
		CheckboxComponent.class,
		CentreComponent.class,
		DragBoxComponent.class,
		DropBoxComponent.class,
		DropdownComponent.class,
		EditFieldComponent.class,
		EmphasisComponent.class,
		EquationComponent.class,
		FlashComponent.class,
		GapComponent.class,
		IfComponent.class,
		IFrameComponent.class,
		ImageComponent.class,
		IndentComponent.class,
		JMEComponent.class,
		LabelComponent.class,
		LayoutGridComponent.class,
		LinkComponent.class,
		ListComponent.class,
		MathMLEquationComponent.class,
		RadioBoxComponent.class,
		RightComponent.class,
		TableComponent.class,
		TextComponent.class,
		TextEquationComponent.class,
		WordSelectComponent.class,
		ParametersComponent.class,
		SummaryLineComponent.class,
		SummaryAttributeComponent.class,
		SummaryForComponent.class,
		AttributeComponent.class,
		ForComponent.class,
		TaskComponent.class,
		ReplaceholderComponent.class,
		RandomComponent.class,
		AnswerComboComponent.class,
		VariableComponent.class,
		LeftComponent.class,
		
		LayoutShuffleComponent.class
		
		// Do not include root component as it can't be created from a tag
	};
	
	/**
	 * Register all known components with a QComponentManager.
	 * @param qcm the QComponentManager to register components with.
	 * @throws OmDeveloperException
	 */
	public static void fill(QComponentManager qcm) throws OmDeveloperException
	{
		for(int i=0;i<COMPONENTCLASSES.length;i++)
		{
			qcm.register(COMPONENTCLASSES[i]);
		}
	}
}
